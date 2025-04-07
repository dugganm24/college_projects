/*
 * Project Name: My Accelerometer Data Recorder
 * Author: Tian Guo
 * Created: November 14, 2024
 * Description: This simple app records accelerometer data from an Android device
 *              and saves it to a CSV file for offline analysis.
 *
 * Note:
 * 1. screen rotation will stop the recording, and flush the remaining writes to the file system.
 * So you will need to turn off the auto rotation in Android
 * 2. the accelerometer data can be downloaded from the Device Explorer under /sdcard/Download
 * Version: 1.0
 */


package edu.wpi.cs.cs4518.saveaccelerometerdata


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity(), SensorEventListener {
	private var sensorManager: SensorManager? = null
	private var accelerometer: Sensor? = null
	private var fileWriter: FileWriter? = null
	private lateinit var statusTextView: TextView
	private lateinit var startButton: Button
	private lateinit var stopButton: Button
	private var isRecording = false

	private val desiredFrequencyHz = 50
	private val samplingPeriodUs = 1_000_000 / desiredFrequencyHz

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		statusTextView = findViewById(R.id.statusTextView)
		startButton = findViewById(R.id.startButton)
		stopButton = findViewById(R.id.stopButton)

		stopButton.isEnabled = false

		startButton.setOnClickListener {
			initialize()
		}

		stopButton.setOnClickListener {
			stopRecording()
		}
	}

	private fun initialize() {
		sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
		accelerometer = sensorManager?.getDefaultSensor(SENSOR_TYPE)

		accelerometer?.let {
			// note that the sampling rate is just a hint; so we might not get 50Hz exact.
			sensorManager?.registerListener(this, it, samplingPeriodUs)
		}

		try {
			val file = createCSVFile()
			fileWriter = FileWriter(file)
			fileWriter?.append("Timestamp,X,Y,Z\n")
			isRecording = true
			statusTextView.text = "Recording..."
			startButton.isEnabled = false
			stopButton.isEnabled = true
		} catch (e: IOException) {
			e.printStackTrace()
			Toast.makeText(this, "Failed to create file", Toast.LENGTH_SHORT).show()
		}
	}

	private fun stopRecording() {
		if (isRecording) {
			sensorManager?.unregisterListener(this)
			try {
				fileWriter?.close()
				Toast.makeText(this, "Recording saved", Toast.LENGTH_SHORT).show()
			} catch (e: IOException) {
				e.printStackTrace()
				Toast.makeText(this, "Failed to save file", Toast.LENGTH_SHORT).show()
			}
			isRecording = false
			statusTextView.text = "Recording stopped"
			startButton.isEnabled = true
			stopButton.isEnabled = false
		}
	}

	@Throws(IOException::class)
	private fun createCSVFile(): File {
		val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
		val fileName = "AccelerometerData_$timestamp.csv"
		val storageDir =
			Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
		if (!storageDir.exists()) {
			storageDir.mkdirs()
		}

		val file = File(storageDir, fileName)
		Log.d("MainActivity", "File saved at: ${file.absolutePath}")

		return file
	}

	override fun onSensorChanged(event: SensorEvent) {
		if (event.sensor.type == SENSOR_TYPE && isRecording) {
			val currentTimestamp = System.currentTimeMillis()

			val x = event.values[0]
			val y = event.values[1]
			val z = event.values[2]


			try {
				fileWriter?.append(
					String.format(
						Locale.getDefault(),
						"%d,%.3f,%.3f,%.3f\n",
						currentTimestamp,
						x,
						y,
						z
					)
				)
			} catch (e: IOException) {
				e.printStackTrace()
			}
		}
	}


	override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
		// Not used
	}

	override fun onDestroy() {
		super.onDestroy()
		stopRecording()
	}

	companion object {
		const val SENSOR_TYPE = Sensor.TYPE_LINEAR_ACCELERATION
	}
}
