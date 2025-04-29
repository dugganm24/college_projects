# Part 3: Supporting Cloud-Based Inference


## Group Members
- William Tyrrell - wftyrrell@wpi.edu
- Michael Duggan - mpduggan@wpi.edu

## Lab Goal
This lab extends the image classification Android app from Part 1 to allow
users to offload image classification tasks to a remote server

---

## Implemented Features

**Updates Made:**  
Code is located at: `Part 3/sourceCode/image_classification-starter`

On-Device Image Classification: Performs image classification directly on the Android device using a TensorFlow Lite model
Updates to existing files for the addition of cloud image classification are shown below

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


**Cloud-Based Image Classification**
Sends captured images to a remote server for classification and displays the cloud inference results
The server is implemented in Part2 of this lab at this location: `Part2/inference_server_project-starter`

Code Location for Implemented features:
  `MainActivity.kt`
  `ImageClassificationHelper.kt`

- `ImageClassificationHelper.kt`:
  - Calls `cloudInference()`.
  - applies probability threshold and converts to `List<Category>`
  - sorts and selects top N results and sends results and inference time via `_classification` SharedFlow

- `MainActivity.kt`
  - `CameraScreen` Composable: Passes frames to `viewModel.classify()`
  - `BottomSheet` Composable: Displays results from `uiState`

 **UI Updates**
Update to allow the user to select between on-device and cloud
Code changes in `MainACtivity` `ViewMOdel` and `UIState`

`MainActivity`:
  - Key is the addition of radio buttons to select between on-device and server inference in `bottomsheet` for classification

`MainViewModel`:
  - `setInferenceMode()`: Updates the inference mode (on-device or cloud) and saves to DataStore
  - `classify()`: Performs image classification (either on-device or cloud) based on the selected inference mode

`UISate`:
  -  Defines new value `inferenceMode` to default to use on-device classification rather than cloud

