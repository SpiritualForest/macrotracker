package com.macrotracker.features.home

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.macrotracker.database.DatabaseRepository
import com.macrotracker.database.entities.MacroEntity
import kotlinx.coroutines.launch

internal data class HomeScreenUiState(
    val macroEntities: List<MacroEntity> = listOf(),
)

class HomeScreenViewModel(
    private val databaseRepository: DatabaseRepository,
) : ViewModel() {

    internal var uiState by mutableStateOf(
        HomeScreenUiState()
    )
        private set

    init {
        monitorDatabaseChanges()
    }

    private fun monitorDatabaseChanges() {
        viewModelScope.launch {
            databaseRepository.getTrackedMacros().collect {
                uiState = uiState.copy(
                    macroEntities = it
                )
            }
        }
    }
}

private const val TAG = "HomeScreenViewModel"