/**
 * Welcome to this shiny step counter app -_-
 */
package edu.wpi.cs.cs4518.stepcounter_starter


import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import edu.wpi.cs.cs4518.stepcounter_starter.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SensorEventListener {

	private lateinit var binding: ActivityMainBinding
	private val viewModel: CounterViewModel by viewModels()

	private lateinit var sensorManager: SensorManager
	private var linearAccelerometer: Sensor? = null

	var isSensorActive = false


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
		linearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

		// Observe step count LiveData
		viewModel.stepCount.observe(this, Observer { count ->
			binding.textViewCounter.text = count.toString()
		})

		// Start tracking sensor data
		binding.buttonStart.setOnClickListener {
			if (!isSensorActive) {
				Log.d(TAG, "Start button clicked")
				linearAccelerometer?.let {
					sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
					isSensorActive = true
					Log.d(TAG, "Sensor registered")
				} ?: Log.e(TAG, "Linear Accelerometer not available")
			} else {
				Log.d(TAG, "Sensor already active, ignoring start request.")
			}
		}


		// Stop tracking sensor data
		binding.buttonStop.setOnClickListener {
			if (isSensorActive){
				Log.d(TAG, "Stop button clicked")
				sensorManager.unregisterListener(this)
				isSensorActive = false
				Log.d(TAG, "Sensor unregistered")
			}
		}
	}

	override fun onSensorChanged(event: SensorEvent?) {
		event?.let {
			viewModel.addSensorData(it.values[0], it.values[1], it.values[2])
		}
	}

	override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
		// Not needed for now
	}

	companion object {
		private const val TAG = "MainActivity"
	}

}