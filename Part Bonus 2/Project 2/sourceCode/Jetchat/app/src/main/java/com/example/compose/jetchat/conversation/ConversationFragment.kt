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

package com.example.compose.jetchat.conversation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.compose.jetchat.MainViewModel
import com.example.compose.jetchat.R
import com.example.compose.jetchat.data.exampleUiState
import com.example.compose.jetchat.theme.JetchatTheme


/**
 * Fragment responsible for displaying the main conversation/chat UI using Jetpack Compose.
 * Integrates Compose content directly within a Fragment and handles navigation events
 * and interactions such as navigating to user profiles or opening the drawer menu
 */

class ConversationFragment : Fragment() {

    private val activityViewModel: MainViewModel by activityViewModels()
    // Activity-scoped ViewModel managing shared UI state and actions like drawer control


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(inflater.context).apply {
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT) // Ensure ComposeView occupies the full available space within the fragment

        // Set Compose UI content including theme, conversation messages, and navigation logic
        setContent {
            JetchatTheme {
                ConversationContent(
                    // Handles user clicks on message authors to navigate to their profiles,
                    // passing the clicked user's ID to the profile destination
                    uiState = exampleUiState,
                    navigateToProfile = { user ->
                        // Click callback
                        val bundle = bundleOf("userId" to user)
                        findNavController().navigate(
                            R.id.nav_profile,
                            bundle
                        )
                    },
                    // Opens the navigation drawer when the top bar's navigation icon is pressed
                    onNavIconPressed = {
                        activityViewModel.openDrawer()
                    }
                )
            }
        }
    }
}
