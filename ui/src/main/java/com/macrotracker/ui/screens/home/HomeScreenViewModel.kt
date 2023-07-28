package com.macrotracker.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.macrotracker.database.DatabaseRepository
import com.macrotracker.database.entities.MacroEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

internal data class HomeScreenUiState(
    val macroEntities: List<MacroEntity> = listOf()
)

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository,
) : ViewModel() {

    internal var uiState by mutableStateOf(
        HomeScreenUiState(
            macroEntities = databaseRepository.getTrackedMacros()
        )
    )
}