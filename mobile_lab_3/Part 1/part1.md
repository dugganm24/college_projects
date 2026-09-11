# Lab 3 Part 1 Answers

### Question 1 - How was the camera preview implemented?
The camera preview is implemented through the CameraX library that is offered in Android's Compose, and it does through through several components. The main entry point that handles camera permissions is the 
CameraScreen composable, which takes a uiState parameter and an onImageAnalyzed callback and checks and request camera permissions using ActivityResultContracts.RequestPermission(). This composable also contains the 
CameraPreview composable, which handles the actual camera display. This CameraPreview composable uses AndroidView to integrate the CameraX PreviewView into the UI and calls bindCameraUseCases() to connect the camera to the UI.
This bindCameraUseCases() function sets up the camera pipeline, which creates a Preview instance, a CameraSelector configured for the back camera, an ImageAnalysis instance that processes frames, then sets the output to GRBA_888
for compatibility with image classification. Each camera frame is then passed to the callback function as an ImageProxy, and the MainActivity passes a function that calls viewModel.classify(imageProxy), which creates a stream
of images from the camera to the classifier. 

### Question 2 - On the bottom sheet, the user can change the settings, e.g., choose a different EfficientNet model while using the app, and the app will start using the new setting. How was this feature implemented?
The app is able to implement real time settings changes using Kotlin Flow. There are several key components, the bottom sheet in MainActivity.kt and the MainViewModel which maintains a MutableStateFlow<Setting> object that 
represents the current settings. When a user changes a setting, the UI element calls the correct function such as onModelSelected or onDelegateSelected. From there, the function calls the corresponding ViewModel method, in 
these examples it would be setModel or setDelegate. After this, the ViewModel correctly updates the setting StateFlow, which has a collector that reacts to changes. This results in an update to the ImageClassificationHelper with
new options. The UI state flow then combines the setting flow with other data sources to set the changes to the UI, and the Compose UI automatically updates.

### Question 3 - How did this app specify which deep learning models to be included in the apk, and where are these two deep learning models stored?
This app includes two deep learning models, which are defined in the ImageClassificationHelper.kt file under the enum class Model. The app includes two EfficientNet models, efficientnet_lite0.tflite and efficientnet_lite2.tflite.
These models are both stored in the assets directory of this app, and the FileUtil.loadMappedFile() function loads these from this directory. The models inlcude metadata with label information which associates the label file
with each model, allowing the app to convert any numerical outputs to their corresponding labels. These model files are included in the APK during the build process which allows the app to perform inference without network
access. 

### Question 4 - What is the data and control flow to run inference on each camera frame and display the result on the bottom sheet? 
The data and control flow to run inference on each camera frame begins at hte CameraScreen, which receives camera frames from CameraX. For each frame it receives, it calls viewModel.classify(imageProxy). In this function, the frame
is converted into a bitmap, rotation information is extracted, and a coroutine is called to perform the classification. In this classification process, the image is resized to match the necessary model input and rotated based on 
camera orientation. This process also normalizes pixel values, and the TensorFlow Lite interpreter runs the model. The results are then mapped to labels, and finally a ClassificationResult is emitted. These classification results
are then combined with settings and error state into a single UiState object, and the MainActivity is updated with this state. 