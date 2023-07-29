package com.macrotracker.ui.screens.tracking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun TrackingScreen(
    viewModel: TrackingScreenViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    Column(modifier = Modifier) {
        TabRow(
            selectedTabIndex = uiState.selectedCategoryIndex,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            uiState.foodCategories.forEachIndexed { index, category ->
                Tab(
                    selected = index == uiState.selectedCategoryIndex,
                    onClick = {
                        viewModel.selectFoodCategory(index)
                    }
                ) {
                    Text(
                        text = category,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(3)
        ) {
            items(uiState.foods) {
                Card {
                    Text(it.name)
                }
            }
        }
    }
}