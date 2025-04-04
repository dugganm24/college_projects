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

package com.google.samples.apps.sunflower.viewmodels

import com.google.samples.apps.sunflower.data.PlantAndGardenPlantings
import java.text.SimpleDateFormat
import java.util.Locale

// ViewModel for a plant and its garden planting
// Responsible for managing the plant and garden planting data
class PlantAndGardenPlantingsViewModel(plantings: PlantAndGardenPlantings) {
    private val plant = checkNotNull(plantings.plant) // Ensure plant is not null
    private val gardenPlanting = plantings.gardenPlantings[0] // Get the first garden planting

    val waterDateString: String = dateFormat.format(gardenPlanting.lastWateringDate.time) // Format last watering date
    val wateringInterval // Get watering interval
        get() = plant.wateringInterval
    val imageUrl // Get image URL
        get() = plant.imageUrl
    val plantName // Get plant name
        get() = plant.name
    val plantDateString: String = dateFormat.format(gardenPlanting.plantDate.time) // Format plant date
    val plantId // Get plant ID
        get() = plant.plantId

    companion object {
        private val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
    }
}