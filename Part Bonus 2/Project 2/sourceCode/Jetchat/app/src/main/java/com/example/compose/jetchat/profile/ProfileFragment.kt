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

package com.example.compose.jetchat.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.compose.jetchat.FunctionalityNotAvailablePopup
import com.example.compose.jetchat.MainViewModel
import com.example.compose.jetchat.R
import com.example.compose.jetchat.components.JetchatAppBar
import com.example.compose.jetchat.theme.JetchatTheme
import android.util.Log


/**
 * Fragment responsible for displaying a user's profile using Jetpack Compose integrated within
 * Android's traditional view system. This fragment demonstrates Compose-View interoperability,
 * ViewModel state observation, and nested scrolling compatibility.
 */
class ProfileFragment : Fragment() {

    // ViewModel scoped to this Fragment which handles user profile state and logic
    private val viewModel: ProfileViewModel by viewModels()
    //Shared activity-level ViewModel for actions such as opening the navigation drawer
    private val activityViewModel: MainViewModel by activityViewModels()

    //Called when the Fragment attaches to its hosting context
    //Retrieves the userId from fragment arguments and sets it in the ViewModel for data loading
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Consider using safe args plugin
        val userId = arguments?.getString("userId")
        if (userId.isNullOrEmpty()) {
            Log.w("ProfileFragment", "No userId passed to ProfileFragment")
        } else {
            Log.d("ProfileFragment", "Received userId: $userId")
        }
        //Set userId to the ViewModel to trigger profile data retrieval
        viewModel.setUserId(userId)
    }

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
    override fun onCreateView(
        // Inflate the fragment's layout sets up ComposeViews for toolbar and profile content
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("ProfileFragment", "onCreateView: Inflating view")
        val rootView: View = inflater.inflate(R.layout.fragment_profile, container, false)

      //  ComposeView integration for rendering the app bar within traditional Android view system
        rootView.findViewById<ComposeView>(R.id.toolbar_compose_view).apply {
            setContent {
                var functionalityNotAvailablePopupShown by remember { mutableStateOf(false) }
                if (functionalityNotAvailablePopupShown) {
                    FunctionalityNotAvailablePopup {
                        Log.i("ProfileFragment", "FunctionalityNotAvailablePopup shown")
                        functionalityNotAvailablePopupShown = false }
                    Log.i("ProfileFragment", "FunctionalityNotAvailablePopup dismissed")
                }

                JetchatTheme {
                  // Compose implementation of the app bar including navigation and actions
                    JetchatAppBar(
                        // Reset the minimum bounds that are passed to the root of a compose tree
                        modifier = Modifier.wrapContentSize(),
                        onNavIconPressed = {
                            Log.d("ProfileFragment", "Nav icon pressed, opening drawer")
                            activityViewModel.openDrawer() },
                        title = { },
                        actions = {
                            // More icon
                            Icon(
                                imageVector = Icons.Outlined.MoreVert,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .clickable(onClick = {
                                        Log.d("ProfileFragment", "'More' icon clicked")
                                        functionalityNotAvailablePopupShown = true
                                    })
                                    .padding(horizontal = 12.dp, vertical = 16.dp)
                                    .height(24.dp),
                                contentDescription = stringResource(id = R.string.more_options)
                            )
                        }
                    )
                }
            }
        }

// Main ComposeView for rendering user profile content based on observed ViewModel state
        rootView.findViewById<ComposeView>(R.id.profile_compose_view).apply {
            // State variable to control the visibility of the popup for unavailable functionality
            setContent {
                //Observes Profile data from view model
                val userData by viewModel.userData.observeAsState()
                //Facilitates nested scrolling behavior between traditional Android views and Compose UI
                val nestedScrollInteropConnection = rememberNestedScrollInteropConnection()

                JetchatTheme {
                    //Conditionally renders error UI or profile screen based on availability of user data
                    if (userData == null) {
                        Log.e("ProfileFragment", "Profile data not available, showing error UI")
                        ProfileError()
                    } else {
                        Log.d("ProfileFragment", "Profile data loaded successfully: ${userData!!.name}")
                        ProfileScreen(
                            userData = userData!!,
                            nestedScrollInteropConnection = nestedScrollInteropConnection
                        )
                    }
                }
            }
        }
        return rootView
    }
}
