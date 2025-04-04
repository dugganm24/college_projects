/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.sunflower

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.samples.apps.sunflower.compose.SunflowerApp
import com.google.samples.apps.sunflower.ui.SunflowerTheme
import dagger.hilt.android.AndroidEntryPoint
import android.util.Log

// GardenActivity is the main activity for Sunflower
// Annotated with @AndroidEntryPoint, triggers Hilt dependency injection for this activity
@AndroidEntryPoint
class GardenActivity : ComponentActivity() {

    // onCreate called when Activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("GardenActivity", "onCreate called")

        // Displaying edge-to-edge
        enableEdgeToEdge()

        // Set compose UI content for this Activity
        setContent {
            SunflowerTheme {
                SunflowerApp() // Provides app's theme
            }
        }
        
    }
}
