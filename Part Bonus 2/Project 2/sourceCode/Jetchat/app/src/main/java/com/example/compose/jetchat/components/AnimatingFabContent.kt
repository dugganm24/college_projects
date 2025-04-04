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

package com.example.compose.jetchat.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.util.lerp
import kotlin.math.roundToInt
import android.util.Log


/**
 * A layout that shows an icon and a text element used as the content for a FAB that extends with
 * an animation.
 *
 * @param icon Composable for the FAB icon
 * @param text Composable for the FAB text
 * @param modifier Modifier for custom styling
 * @param extended Boolean indicating whether the FAB is in extended or collapsed state
 *
 *
 */
@Composable
fun AnimatingFabContent(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    extended: Boolean = true
) {
    // Determines the target state (extended or collapsed) for the FAB animation
    val currentState = if (extended) ExpandableFabStates.Extended else ExpandableFabStates.Collapsed
    Log.d("AnimatingFabContent", "Current FAB state: $currentState")

    // Manages animation states for the FAB
    val transition = updateTransition(currentState, "fab_transition")

    // Animates the opacity of the text content
    val textOpacity by transition.animateFloat(
        transitionSpec = {
            // Defines the animation timing for text opacity
            if (targetState == ExpandableFabStates.Collapsed) {
                Log.d("AnimatingFabContent", "Animating text fade-out")
                tween(
                    easing = LinearEasing,
                    durationMillis = (transitionDuration / 12f * 5).roundToInt() // 5 / 12 frames
                    // Collapsing: immediate fade-out over 5/12 of total duration
                )
            } else {
                Log.d("AnimatingFabContent", "Animating text fade-in")
                tween(
                    easing = LinearEasing,
                    delayMillis = (transitionDuration / 3f).roundToInt(), // 4 / 12 frames
                    durationMillis = (transitionDuration / 12f * 5).roundToInt() // 5 / 12 frames
                    // Expanding: delayed fade-in starting at 4/12, lasting 5/12 of total duration
                )
            }
        },
        label = "fab_text_opacity"
    ) { state ->
        if (state == ExpandableFabStates.Collapsed) {
            0f
        } else {
            1f
        }
    }
    // Animates FAB width from icon-only (collapsed) to full icon and text (expanded)
    val fabWidthFactor by transition.animateFloat(
        // Defines animation duration and easing for FAB width changes in both directions
        transitionSpec = {
            Log.d("AnimatingFabContent", "Animating width factor for state: $targetState")
            if (targetState == ExpandableFabStates.Collapsed) {
                tween(
                    easing = FastOutSlowInEasing,
                    durationMillis = transitionDuration
                )
            } else {
                tween(
                    easing = FastOutSlowInEasing,
                    durationMillis = transitionDuration
                )
            }
        },
        label = "fab_width_factor"
    ) { state ->
        if (state == ExpandableFabStates.Collapsed) {
            0f
        } else {
            1f
        }
    }
    // Deferring reads using lambdas instead of Floats here can improve performance,
    // preventing recompositions.
    IconAndTextRow(
        icon,
        text,
        { textOpacity },
        { fabWidthFactor },
        modifier = modifier
    )
}
/**
 * Layout composable arranging icon and text horizontally with dynamic spacing.
 * Adjusts width and opacity based on animation progress, achieving smooth transitions.
 *
 * @param icon Composable icon content
 * @param text Composable text content
 * @param opacityProgress Lambda providing current opacity animation state
 * @param widthProgress Lambda providing current width animation state
 * @param modifier Modifier for custom styling
 */
@Composable
private fun IconAndTextRow(
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    opacityProgress: () -> Float, // Lambdas instead of Floats, to defer read
    widthProgress: () -> Float,
    modifier: Modifier
) {
    Layout(
        modifier = modifier,
        content = {
            icon()
            Box(modifier = Modifier.graphicsLayer { alpha = opacityProgress() }) {
                text()
            }
        }
    ) { measurables, constraints ->

        val iconPlaceable = measurables[0].measure(constraints)
        val textPlaceable = measurables[1].measure(constraints)

        val height = constraints.maxHeight

        // FAB has an aspect ratio of 1 so the initial width is the height
        val initialWidth = height.toFloat()

        // Use it to get the padding
        val iconPadding = (initialWidth - iconPlaceable.width) / 2f

        // The full width will be : padding + icon + padding + text + padding
        val expandedWidth = iconPlaceable.width + textPlaceable.width + iconPadding * 3

        // Apply the animation factor to go from initialWidth to fullWidth
        val width = lerp(initialWidth, expandedWidth, widthProgress())
        Log.d("IconAndTextRow", "InitialWidth: $initialWidth, ExpandedWidth: $expandedWidth, CurrentWidth: $width")

        layout(width.roundToInt(), height) {
            iconPlaceable.place(
                iconPadding.roundToInt(),
                constraints.maxHeight / 2 - iconPlaceable.height / 2
            )
            textPlaceable.place(
                (iconPlaceable.width + iconPadding * 2).roundToInt(),
                constraints.maxHeight / 2 - textPlaceable.height / 2
            )
        }
    }
}

private enum class ExpandableFabStates { Collapsed, Extended }

private const val transitionDuration = 200
