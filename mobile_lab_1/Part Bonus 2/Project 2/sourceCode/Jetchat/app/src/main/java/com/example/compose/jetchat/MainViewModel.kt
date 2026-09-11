/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.compose.jetchat

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Log

/**
 * Used to communicate between screens.
 * ViewModel managing UI state shared across screens primarily the navigation drawer state
 */
class MainViewModel : ViewModel() {

    private val _drawerShouldBeOpened = MutableStateFlow(false) // Backing StateFlow tracking if the navigation drawer should be opened
    val drawerShouldBeOpened = _drawerShouldBeOpened.asStateFlow() // Exposed StateFlow for observing drawer open actions from UI components

    // Signals that the navigation drawer should be opened
    fun openDrawer() {
        Log.i("MainViewModel", "openDrawer() called - drawer open signal set to true")
        _drawerShouldBeOpened.value = true
    }
    // Resets drawer state after the drawer has been opened or the action has been consumed
    fun resetOpenDrawerAction() {
        Log.i("MainViewModel", "resetOpenDrawerAction() called - drawer open signal reset to false")
        _drawerShouldBeOpened.value = false
    }
}
