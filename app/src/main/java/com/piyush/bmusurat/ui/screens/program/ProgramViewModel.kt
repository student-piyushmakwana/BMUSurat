package com.piyush.bmusurat.ui.screens.program

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piyush.bmusurat.data.models.ProgramResponse
import com.piyush.bmusurat.data.repository.ProgramRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProgramUiState(
    val isLoading: Boolean = false,
    val data: ProgramResponse? = null,
    val error: String? = null
)

@HiltViewModel
class ProgramViewModel @Inject constructor(
    private val repository: ProgramRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgramUiState())
    val uiState: StateFlow<ProgramUiState> = _uiState.asStateFlow()

    private val shortName: String? = savedStateHandle["shortName"]

    init {
        loadData()
    }

    fun loadData() {
        if (shortName == null) {
            _uiState.update { it.copy(error = "Invalid Program ID") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.getProgramDetails(shortName)
                .onSuccess { response ->
                    _uiState.update { it.copy(isLoading = false, data = response) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }
}