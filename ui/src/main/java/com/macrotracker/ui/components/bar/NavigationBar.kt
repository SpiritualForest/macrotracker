package com.macrotracker.ui.components.bar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.macrotracker.ui.R
import com.macrotracker.ui.components.theme.MacroTrackerTheme
import com.macrotracker.ui.data.NavigationItem

@Composable
fun NavigationBar(
    items: List<NavigationItem>,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        items.forEach { navItem ->
            Column(
                modifier = Modifier.clickable { navItem.onClick() },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    painter = painterResource(id = navItem.iconResource),
                    contentDescription = navItem.contentDescription,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = navItem.title,
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    val items = listOf(
        NavigationItem(
            title = "Item",
            iconResource = R.drawable.ic_launcher_background,
        ),
        NavigationItem(
            title = "Item",
            iconResource = R.drawable.ic_launcher_background,
        ),
        NavigationItem(
            title = "Item",
            iconResource = R.drawable.ic_launcher_background,
        )
    )
    MacroTrackerTheme {
        NavigationBar(items = items)
    }
}