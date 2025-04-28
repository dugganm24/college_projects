# Part 3: Supporting CLoud-Based Inference


## Group Members
- William Tyrrell - wftyrrell@wpi.edu
- Michael Duggan - mpduggan@wpi.edu

## Lab Goal
This app classifies images using either the device's TensorFlow Lite 
model or a cloud-based inference server, allowing users to compare performance and accuracy

---

## Implemented Features

**Updates Made:**  
Code is located at: `Part 2/sourceCode/SaveAccelerometerData-starter/app/src/main/java/edu/wpi/cs/cs4518/saveaccelerometerdata/MainActivity.kt`

1. On-Device Image Classification
Performs image classification directly on the Android device using a TensorFlow Lite model

Code Location:

`MainActivity.kt`
`ImageClassificationHelper.kt`

*Key Functions and Updates:*

`ImageClassificationHelper.kt`:

- `initClassifier()`: Initializes the TensorFlow Lite interpreter with the selected model and delegate
- `classifyWithTFLite()`: Performs inference using the TensorFlow Lite interpreter

- `performOnDeviceInference(bitmap: Bitmap, rotationDegrees: Int)`:
  - Preprocesses input bitmap 
  - Converts it to a TensorImage
  - Calls `classifyWithTFLite()`
  - Applies probability threshold
  - Combines labels with scores and sorts them
  - results and inference time via _classification SharedFlow

- `MainActivity.kt`:
  - `CameraScreen` Composable: Passes frames to `viewModel.classify()`.
  - `BottomSheet` Composable: Displays results from uiState


2. Cloud-Based Image Classification
Sends captured images to a remote server for classification and displays the cloud inference results
The sever is implemented in Part2 of this lab at this location: ``

Code Location for Implemented features:
  `MainActivity.kt`
  `ImageClassificationHelper.kt`

- `ImageClassificationHelper.kt`:
  - Calls `cloudInference()`.
  - applies probability threshold and converts to `List<Category>`
  - sorts and selects top N results and send results and inference time via `_classification` SharedFlow

- `MainActivity.kt`
  - `CameraScreen` Composable: Passes frames to `viewModel.classify()`
  - `BottomSheet` Composable: Displays results from `uiState`

3. UI Updates

Code changes in `MainACtivity`

- Key is the additon of radio buttons to select between on-device and cloud inference in `bottomsheet`