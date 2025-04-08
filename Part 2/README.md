# Part 2: A Simple Sensor Recording Android App


## Group Members
- William Tyrrell - wftyrrell@wpi.edu
- Michael Duggan - mpduggan@wpi.edu

## Lab Goal
This lab will implement a simple Android app that can interact with the Sensor
Framework to save accelerometer data to the Android file system

---

## Implemented Feature: App now records and outputs sensor data for the accelerometer and linear accelerometer

**Updates Made:**  
Code is located at: `Part 1/GeoQuiz-Starter/GeoQuiz/sourceCode/app/src/main/java/com/bignerdranch/android/geoquiz`

We Modified the app’s MainActivity so that both the regular accelerometer (`Sensor.TYPE_ACCELEROMETER`) 
and the linear accelerometer (`Sensor.TYPE_LINEAR_ACCELERATION`) 
are recorded at the same time and written to two separate CSV files in the device’s Downloads folder

The following changes were made:

- Two `FileWriter` variables were added:
    - `linearFileWriter` for linear accelerometer data
    - `normalFileWriter` for regular accelerometer data

- Both sensors from the Android `SensorManager` are called in `initialize()`

- Two separate CSV files are created at the start of each recording session using the function `createCSVFile()`.  
  
- This function uses a timestamp and a sensor-specific prefix (`"Accelerometer"` or `"LinearAccelerometer"`) to generate a unique file name for each sensor

- Sensor data is logged in `onSensorChanged()`.  
  
- Each sensor’s output is routed to the correct file writer

- `stopRecording()` function calls `.close()` on both file writers (`linearFileWriter` and `normalFileWriter`).  
This ensures all buffered data is saved and no data is lost when recording stops


