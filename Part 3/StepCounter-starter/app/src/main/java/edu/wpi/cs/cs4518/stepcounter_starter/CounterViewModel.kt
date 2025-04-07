package edu.wpi.cs.cs4518.stepcounter_starter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CounterViewModel : ViewModel() {

	// Live data
	private val _stepCount = MutableLiveData(0)
	val stepCount: LiveData<Int> = _stepCount

	// TODO: you will need to implement this function so that
	// 1. each sensor data will be saved in a thread-safe data structure
	// 2. once we have accumulated 50 sensor data samples, call the processSensorData
	fun addSensorData(x: Float, y: Float, z: Float) {
		return
	}

	// TODO:
	//  1. pull the sensor data samples and then pass them to the step detection algorithm
	//  2. because the algorithm might be compute or I/O intensive,
	//  you will should run the algorithm in a coroutine.
	//      2.1: if you want to do the mobile-only architecture, use StepCounterAlgorithm
	//      2.2: if you want to try the server-based architecture, then the networking code
	//          can also go here (i.e., send request and then parse response)
	//  3. Once you get the detected step counts, use it to update the current step
	private fun processSensorData() {
		return
	}

	companion object {
		private const val TAG = "CounterViewModel"
	}
}