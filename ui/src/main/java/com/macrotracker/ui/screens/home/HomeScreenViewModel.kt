package com.macrotracker.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.macrotracker.database.DatabaseRepository
import com.macrotracker.database.entities.MacroEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

internal data class HomeScreenUiState(
    val macroEntities: List<MacroEntity> = listOf()
)

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository,
) : ViewModel() {

    internal var uiState by mutableStateOf(HomeScreenUiState())
        private set

    init {
        monitorDatabaseChanges()
    }

    private fun monitorDatabaseChanges() {
        viewModelScope.launch(IO) {
            databaseRepository.getTrackedMacros().collect {
                uiState = uiState.copy(
                    macroEntities = it
                )
            }
        }
    }
}

private const val TAG = "HomeScreenViewModel"