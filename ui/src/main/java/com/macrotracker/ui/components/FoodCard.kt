package com.macrotracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.macrotracker.database.FoodItem

@Composable
fun FoodCard(
    data: FoodItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .clickable { onClick() },
    ) {
        Text(
            text = data.name,
            modifier = Modifier.padding(8.dp)
        )
        // TODO: image
    }
}