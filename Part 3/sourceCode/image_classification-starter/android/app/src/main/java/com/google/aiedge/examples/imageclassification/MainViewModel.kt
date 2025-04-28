/*
 * Copyright 2024 The Google AI Edge Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.aiedge.examples.imageclassification

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

// Import the InferenceMode enum
import com.google.aiedge.examples.imageclassification.ImageClassificationHelper.Options.InferenceMode
import org.json.JSONObject


class MainViewModel(private val imageClassificationHelper: ImageClassificationHelper) :
    ViewModel() {
    companion object {
        private const val CLOUD_ENDPOINT = "http://10.0.2.2:5050/predict"
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaTypeOrNull()
        private val IMAGE_MEDIA_TYPE = "image/jpeg".toMediaTypeOrNull()
        private val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        // Function to parse the JSON response from the cloud
        private fun parseCloudResponse(responseBody: String?): List<Pair<String, Float>>? {
            Log.d("CloudInference", "Parsing cloud response: $responseBody")
            if (responseBody == null) {
                Log.w("CloudInference", "Response body is null, cannot parse.")
                return null
            }
            return try {
                val jsonObject = JSONObject(responseBody)
                val predictionsArray = jsonObject.getJSONArray("predictions")
                val results = mutableListOf<Pair<String, Float>>()
                for (i in 0 until predictionsArray.length()) {
                    val predictionObject = predictionsArray.getJSONObject(i)
                    val label = predictionObject.getString("prediction_label")
                    val score = predictionObject.getDouble("score").toFloat()
                    Log.d("CloudInference", "Parsed result: Label=$label, Score=$score")
                    results.add(label to score)
                }
                Log.d("CloudInference", "Parsed results list: $results")
                results
            } catch (e: Exception) {
                Log.e("CloudInference", "Error parsing cloud response: ${e.message}", e)
                null
            }
        }

        fun getFactory(context: Context) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                // Pass the cloudInference lambda to the ImageClassificationHelper
                val imageClassificationHelper = ImageClassificationHelper(
                    context = context,
                    cloudInference = { bitmap ->
                        Log.d("CloudInference", "Cloud inference lambda invoked.")
                        // Cloud inference logic using OkHttp
                        if (CLOUD_ENDPOINT == "YOUR_CLOUD_INFERENCE_ENDPOINT") { // Check for the placeholder
                            Log.w("MainViewModel", "Cloud endpoint not configured. Please update CLOUD_ENDPOINT in MainViewModel.")
                            null
                        } else {
                            try {
                                val tempFile = File(context.cacheDir, "image_temp.jpg")
                                tempFile.createNewFile()
                                val bos = FileOutputStream(tempFile)
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos)
                                bos.flush()
                                bos.close()

                                Log.d("CloudInference", "Image compressed and temporary file created: ${tempFile.absolutePath}")

                                val requestBody = MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart(
                                        "file", // Adjust the key based on your API requirements
                                        tempFile.name,
                                        tempFile.asRequestBody(IMAGE_MEDIA_TYPE)
                                    )
                                    .build()

                                val request = Request.Builder()
                                    .url(CLOUD_ENDPOINT)
                                    .post(requestBody)
                                    .build()

                                val response = okHttpClient.newCall(request).execute()
                                val responseBody = response.body?.string()
                                tempFile.delete() // Clean up the temporary file
                                Log.d("CloudInference", "HTTP response received. Code: ${response.code}, Body: $responseBody")
                                if (response.isSuccessful) {
                                    return@ImageClassificationHelper parseCloudResponse(responseBody)
                                } else {
                                    Log.e("MainViewModel", "Cloud inference failed with code: ${response.code} - ${response.message}")
                                    return@ImageClassificationHelper null
                                }
                            } catch (e: Exception) {
                                Log.e("MainViewModel", "Cloud inference error: ${e.message}")
                                return@ImageClassificationHelper null
                            }
                        }
                    }
                )
                return MainViewModel(imageClassificationHelper) as T
            }
        }
    }


    private var classificationJob: Job? = null

    private val setting = MutableStateFlow(Setting())
        .apply {
            viewModelScope.launch {
                // this will be called when the setting state is updated
                collectLatest {
                    imageClassificationHelper.setOptions(
                        ImageClassificationHelper.Options(
                            model = it.model,
                            delegate = it.delegate,
                            resultCount = it.resultCount,
                            probabilityThreshold = it.threshold,
                            // Pass the inferenceMode to the ImageClassificationHelper options
                            inferenceMode = it.inferenceMode
                        )
                    )
                    imageClassificationHelper.initClassifier()
                }
            }
        }


    private val errorMessage = MutableStateFlow<Throwable?>(null).also {
        viewModelScope.launch {
            imageClassificationHelper.error.collect(it)
        }
    }

    val uiState: StateFlow<UiState> = combine(
        imageClassificationHelper.classification
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                ImageClassificationHelper.ClassificationResult(emptyList(), 0L)
            ),
        setting.filterNotNull(),
        errorMessage,
    ) { result, setting, error ->
        UiState(
            inferenceTime = result.inferenceTime,
            categories = result.categories,
            setting = setting,
            errorMessage = error?.message
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())


    /** Start classify an image.
     * @param imageProxy contain `imageBitMap` and imageInfo as `image rotation degrees`.
     *
     */
    fun classify(imageProxy: ImageProxy) {
        classificationJob = viewModelScope.launch {
            imageClassificationHelper.classify(
                imageProxy.toBitmap(),
                imageProxy.imageInfo.rotationDegrees,
            )
            imageProxy.close()
        }
    }

    /** Stop current classification */
    fun stopClassify() {
        classificationJob?.cancel()
    }

    /** Set [ImageClassificationHelper.Delegate] (CPU/NNAPI) for ImageSegmentationHelper*/
    fun setDelegate(delegate: ImageClassificationHelper.Delegate) {
        setting.update { it.copy(delegate = delegate) }
    }

    /** Set [ImageClassificationHelper.Model] for ImageSegmentationHelper*/
    fun setModel(model: ImageClassificationHelper.Model) {
        setting.update { it.copy(model = model) }
    }

    /** Set Number of output classes of the [ImageClassificationHelper.Model].  */
    fun setNumberOfResult(numResult: Int) {
        setting.update { it.copy(resultCount = numResult) }
    }

    /** Set the threshold so the label can display score */
    fun setThreshold(threshold: Float) {
        setting.update { it.copy(threshold = threshold) }
    }

    /** Set the inference mode (ON_DEVICE or CLOUD) */
    fun setInferenceMode(inferenceMode: InferenceMode) {
        setting.update { it.copy(inferenceMode = inferenceMode) }
    }

    /** Clear error message after it has been consumed*/
    fun errorMessageShown() {
        errorMessage.update { null }
    }
}