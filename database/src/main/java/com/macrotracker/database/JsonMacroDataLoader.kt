package com.macrotracker.database

import android.content.Context
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

/* JSON data parsing */

@Serializable
data class FoodItem(
    val name: String,
    val calories: Int,
    val fat: Float,
    val fiber: Float,
    val protein: Float,
    val carbs: Float,
    val water: Float,
    val sodium: Float,
)

@Serializable
data class FoodData(
    val vegetables: List<FoodItem>,
    val fruits: List<FoodItem>,
    val grains: List<FoodItem>,
    val beans: List<FoodItem>,
)

@OptIn(ExperimentalSerializationApi::class)
fun loadMacroJsonData(context: Context): FoodData {
    // Load the Macros JSON file
    val stream = context.assets.open(MACROS_FILE)
    val jsonData: FoodData = Json.decodeFromStream(stream)
    stream.close()
    return jsonData
}

enum class FoodCategory {
    Vegetables,
    Fruits,
    Grains,
    Beans,
}

private const val MACROS_FILE = "foodData.json"