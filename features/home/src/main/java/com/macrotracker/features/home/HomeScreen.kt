package com.macrotracker.features.home

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.macrotracker.ui.components.card.MacroCard

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel,
    onFabClick: () -> Unit,
    onMacroItemClick: (Int) -> Unit,
) {
    val uiState = viewModel.uiState
    Log.d(TAG, "HomeScreen recomposed")
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onFabClick,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = ""
                )
            }
        },
    ) { paddingValues ->
        if (uiState.macroEntities.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Nothing tracked yet",
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValues)
            ) {
                items(uiState.macroEntities) {
                    MacroCard(
                        data = it,
                        modifier = Modifier.padding(16.dp),
                        onClick = {
                            onMacroItemClick(it.date)
                        }
                    )
                }
            }
        }
    }
}

private const val TAG = "HomeScreen"