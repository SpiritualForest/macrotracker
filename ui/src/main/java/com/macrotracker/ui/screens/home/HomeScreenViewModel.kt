package com.macrotracker.ui.screens.home

import android.util.Log
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

    internal fun getMacros() {
        viewModelScope.launch(IO) {
            val macros = databaseRepository.getTrackedMacros()
            uiState = uiState.copy(
                macroEntities = macros
            )
        }
    }
}

private const val TAG = "HomeScreenViewModel"