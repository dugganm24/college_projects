package edu.wpi.cs.cs4518.stepcounter_starter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class CounterViewModel : ViewModel() {

	// Live data
	private val _stepCount = MutableLiveData(0)
	val stepCount: LiveData<Int> = _stepCount

	private val sensorDataBuffer = mutableListOf<FloatArray>()
	private var timestampBuffer = mutableListOf<Long>()
	private val lock = Any()

	// Track server connection state
	private var isConnected = false

	// TODO: you will need to implement this function so that
	// 1. each sensor data will be saved in a thread-safe data structure
	// 2. once we have accumulated 50 sensor data samples, call the processSensorData
	fun addSensorData(x: Float, y: Float, z: Float) {
		synchronized(lock) {
			// Save the current timestamp along with the data
			sensorDataBuffer.add(floatArrayOf(x, y, z))
			timestampBuffer.add(System.currentTimeMillis())

			// Process data in batches of 50 samples
			if (sensorDataBuffer.size >= 50) {
				val dataBatch = sensorDataBuffer.toList()
				val timestampBatch = timestampBuffer.toList()

				// Clear buffers after copying
				sensorDataBuffer.clear()
				timestampBuffer.clear()

				// Process the data
				processSensorData(dataBatch, timestampBatch)
			}
		}
	}

	// TODO:
	//  1. pull the sensor data samples and then pass them to the step detection algorithm
	//  2. because the algorithm might be compute or I/O intensive,
	//  you will should run the algorithm in a coroutine.
	//      2.1: if you want to do the mobile-only architecture, use StepCounterAlgorithm
	//      2.2: if you want to try the server-based architecture, then the networking code
	//          can also go here (i.e., send request and then parse response)
	//  3. Once you get the detected step counts, use it to update the current step
	private fun processSensorData(data: List<FloatArray>, timestamps: List<Long>) {
		// Using coroutine for network operations
		CoroutineScope(Dispatchers.IO).launch {
			try {
				// Create properly formatted JSON for server.py
				val json = buildJson(data, timestamps)
				Log.d(TAG, "Sending data to server: ${json.take(100)}...")

				// Create HTTP client
				val client = OkHttpClient.Builder()
					.connectTimeout(5, TimeUnit.SECONDS)
					.readTimeout(10, TimeUnit.SECONDS)
					.build()

				// Prepare request
				val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
				val request = Request.Builder()
					.url("http://10.0.2.2:5050/data")
					.post(requestBody)
					.build()

				// Execute request
				val response = client.newCall(request).execute()
				val body = response.body?.string()
				Log.d(TAG, "Server response: $body")

				// Process response
				body?.let {
					val jsonResponse = JSONObject(it)
					if (jsonResponse.has("results")) {
						// Using the "results" field as per your server implementation
						val newSteps = jsonResponse.getInt("results")
						Log.d(TAG, "New steps detected: $newSteps")

						// Update UI on the main thread
						withContext(Dispatchers.Main) {
							_stepCount.value = (_stepCount.value ?: 0) + newSteps
						}
					} else {
						Log.e(TAG, "Invalid response format: missing 'results' field")
					}
				}
			} catch (e: Exception) {
				Log.e(TAG, "Network error: ${e.message}", e)
			}
		}
	}

	// Create properly formatted JSON for the server
	private fun buildJson(data: List<FloatArray>, timestamps: List<Long>): String {
		val jsonArray = JSONArray()

		// Combine acceleration values with timestamps
		for (i in data.indices) {
			val dataPoint = JSONObject()
			dataPoint.put("timestamp", timestamps[i])
			dataPoint.put("x", data[i][0])
			dataPoint.put("y", data[i][1])
			dataPoint.put("z", data[i][2])
			jsonArray.put(dataPoint)
		}

		return jsonArray.toString()
	}

	// Reset step counter
	fun resetStepCount() {
		_stepCount.value = 0
	}

	companion object {
		private const val TAG = "CounterViewModel"
	}
}