package com.macrotracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.macrotracker.database.entities.MacroEntity
import com.macrotracker.database.toFormattedDate

@Composable
fun MacroCard(
    data: MacroEntity,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    ElevatedCard(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Text(
            text = data.date.toFormattedDate(),
            style = TextStyle.Default.copy(
                fontSize = 20.sp
            ),
            modifier = Modifier.padding(8.dp)
        )
        Divider()
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            data.toStringMap().forEach {
                Text(
                    text = "${it.key}: ${it.value}",
                )
            }
        }
    }
}

private fun MacroEntity.toStringMap(): Map<String, String> {
    return mapOf(
        "Calories" to "${this.calories}",
        "Fat" to "${this.fat}",
        "Carbs" to "${this.carbs}",
        "Protein" to "${this.protein}",
        "Fiber" to "${this.fiber}",
        "Water" to "${this.water}",
        "Sodium" to "${this.sodium}"
    )
}

@Preview
@Composable
private fun Preview() {
    MacroCard(
        data = MacroEntity(
            calories = 1000,
            fat = 1f,
            carbs = 1f,
            fiber = 1f,
            protein = 1f,
            water = 1f,
            sodium = 1f,
            date = 18564
        )
    )
}