package com.macrotracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            text = data.date.toFormattedDate()
        )
    }
}
