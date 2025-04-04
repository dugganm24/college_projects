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

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material3.DrawerValue.Closed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.compose.jetchat.components.JetchatDrawer
import com.example.compose.jetchat.databinding.ContentMainBinding
import kotlinx.coroutines.launch
import android.util.Log

/**
 * Main activity for the app.
 * Sets up Compose-based UI integrated with Jetpack Navigation and manages
 * the state of the drawer, navigation actions, and edge-to-edge UI appearance.
 */
class NavActivity : AppCompatActivity() {
//Main activity ViewModel managing shared UI state such as drawer state
    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge() // Enables edge-to-edge content layout for a more immersive experience
        super.onCreate(savedInstanceState)
        Log.i("NavActivity", "onCreate: Activity created")
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets -> insets }

        // Sets ComposeView as the activity content, allowing integration of Jetpack Compose UI
        setContentView(
            ComposeView(this).apply {
                consumeWindowInsets = false
                setContent {
                    //Manages the open/closed state of the navigation drawer
                    val drawerState = rememberDrawerState(initialValue = Closed)
                    // Observes the drawer open state from ViewModel to trigger drawer interactions
                    val drawerOpen by viewModel.drawerShouldBeOpened
                        .collectAsStateWithLifecycle()
                    // Tracks currently selected drawer menu item, such as chat rooms or profiles
                    var selectedMenu by remember { mutableStateOf("composers") }
                    if (drawerOpen) {
                        Log.i("NavActivity", "Opening drawer from ViewModel trigger")
                        // Open drawer and reset state in VM.
                        LaunchedEffect(Unit) {
                            // wrap in try-finally to handle interruption whiles opening drawer
                            try {
                                drawerState.open()
                            } finally {
                                viewModel.resetOpenDrawerAction()
                            }
                        }
                    }

                    val scope = rememberCoroutineScope() // Coroutine scope for performing asynchronous drawer operations
                  // Compose-based navigation drawer handling menu selection and navigation actions
                    JetchatDrawer(
                        drawerState = drawerState,
                        selectedMenu = selectedMenu,
                        // Navigates back to the main chat list when a chat menu item is clicked, closing the drawer afterward
                        onChatClicked = {
                            Log.i("NavActivity", "Navigating to chat list (Home)")
                            findNavController().popBackStack(R.id.nav_home, false)
                            scope.launch {
                                drawerState.close()
                            }
                            selectedMenu = it
                        },
                        // Navigates to selected user's profile, passing the required user ID, and closes the drawer
                        onProfileClicked = {
                            val bundle = bundleOf("userId" to it)
                            findNavController().navigate(R.id.nav_profile, bundle)
                            scope.launch {
                                drawerState.close()
                                Log.i("NavActivity", "Drawer closed after chat click")
                            }
                            selectedMenu = it
                        }
                    ) {
                        // Integrates XML-based navigation host fragment layout with Compose-based UI
                        AndroidViewBinding(ContentMainBinding::inflate)
                    }
                }
            }
        )
    }

// Handles "up" navigation delegating to the NavController or defaulting to superclass behavior
    override fun onSupportNavigateUp(): Boolean {
        return findNavController().navigateUp() || super.onSupportNavigateUp()
    }

    /**
     * Utility function for retrieving the NavController associated with the current NavHostFragment
     * See https://issuetracker.google.com/142847973
     */
    private fun findNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }
}
