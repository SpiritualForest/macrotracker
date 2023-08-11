package com.macrotracker.ui.screens.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.macrotracker.ui.components.FoodCard
import com.macrotracker.ui.R

@Composable
fun TrackingScreen(
    viewModel: TrackingScreenViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    var showWeightInput by remember {
        mutableStateOf(false)
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TabRow(
            selectedTabIndex = uiState.selectedCategoryIndex,
            modifier = Modifier.padding(vertical = 8.dp),
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
        if (showWeightInput) {
            WeightInput(
                onAddClick = { weight ->
                    showWeightInput = false
                    viewModel.addFood(weight)
                },
                onCancelClick = { showWeightInput = false }
            )
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
        ) {
            items(uiState.foods) {
                FoodCard(data = it) {
                    showWeightInput = true
                    viewModel.selectFoodToTrack(it)
                }
            }
        }
    }
}

@Composable
private fun WeightInput(
    onAddClick: (weight: Int) -> Unit,
    onCancelClick: () -> Unit,
) {
    Dialog(onDismissRequest = onCancelClick) {
        var weight by remember {
            mutableStateOf(0)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.weight_input_text)
                )
                TextField(
                    value = if (weight > 0) weight.toString() else "",
                    onValueChange = {
                        if (it.isEmpty()) {
                            weight = 0
                        } else {
                            val value = it.toIntOrNull()
                            value?.let {
                                weight = value
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onAddClick(weight) }
                ) {
                    Text(stringResource(id = R.string.add_button_text))
                }
                Button(
                    onClick = onCancelClick
                ) {
                    Text(stringResource(id = R.string.cancel_button_text))
                }
            }
        }
    }
}

private const val TAG = "TrackingScreen"