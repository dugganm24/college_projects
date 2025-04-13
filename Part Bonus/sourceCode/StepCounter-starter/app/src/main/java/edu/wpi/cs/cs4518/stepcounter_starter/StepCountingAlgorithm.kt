package edu.wpi.cs.cs4518.stepcounter_starter

import android.util.Log
import kotlin.random.Random

object StepCounterAlgorithm {
	private const val TAG = "StepCounterAlgorithm"

	//TODO: you will implement the *local* step detection algorithm here
	// Remember that you are recording (x,y,z) data, but the detection should be based on the magnitude
	// Also you should check the documentation of the jdsp as mentioned in the project description
	// currently it just returns some random number, which is useful to testing the overall workflow
	fun detectSteps(sensorData: List<FloatArray>): Int {
		val randomSteps = Random.nextInt(1, 10) // Generate a random step count between 1 and 10
		Log.d(TAG, "Step detection completed. Random steps detected: $randomSteps")
		return randomSteps
	}

}