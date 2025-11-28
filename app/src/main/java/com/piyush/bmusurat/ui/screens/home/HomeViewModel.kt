package com.piyush.bmusurat.ui.screens.home

import android.content.Context
import android.util.Log
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piyush.bmusurat.data.models.HomeResponse
import com.piyush.bmusurat.data.repository.AuthRepository
import com.piyush.bmusurat.data.repository.HomeRepository
import com.piyush.bmusurat.util.ConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val data: HomeResponse? = null,
    val error: String? = null,
    val isNetworkAvailable: Boolean = true,
    val isSignInLoading: Boolean = false,
    val signInSuccess: Boolean = false,
    val signInError: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val authRepository: AuthRepository,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val TAG = "HomeViewModel"

    init {
        Log.d(TAG, "ViewModel initialized. Triggering initial data load.")
        observeNetworkStatus()
        loadHomeData()
    }

    private fun observeNetworkStatus() {
        connectivityObserver.observe().onEach { status ->
            val isAvailable = status == ConnectivityObserver.Status.Available
            Log.d(TAG, "Network status changed: $status")
            _uiState.update { it.copy(isNetworkAvailable = isAvailable) }

            if (isAvailable && _uiState.value.data == null) {
                loadHomeData()
            }
        }.launchIn(viewModelScope)
    }

    fun loadHomeData() {
        viewModelScope.launch {
            Log.d(TAG, "loadHomeData called.")
            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.getHomeData()
                .onSuccess { responseData ->
                    Log.d(TAG, "Data loaded successfully (Success=${responseData.success}).")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            data = responseData,
                            error = null
                        )
                    }
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to load data from Network or Cache: ${exception.message}", exception)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Unable to load data. Please check your internet connection."
                        )
                    }
                }
        }
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSignInLoading = true, signInError = null, signInSuccess = false) }

            val result = authRepository.signInWithGoogle(context)

            result.onSuccess { uniqueId ->
                Log.d(TAG, "Google Sign-In Success. Unique Google ID (sub): $uniqueId")

                _uiState.update {
                    it.copy(isSignInLoading = false, signInSuccess = true)
                }
            }.onFailure { error ->
                if (error is GetCredentialCancellationException) {
                    Log.d(TAG, "User cancelled sign-in")
                    _uiState.update { it.copy(isSignInLoading = false) }
                } else {
                    Log.e(TAG, "Google Sign-In Failed", error)
                    _uiState.update {
                        it.copy(isSignInLoading = false, signInError = "Sign-in failed. Please try again.")
                    }
                }
            }
        }
    }

    fun resetSignInState() {
        _uiState.update { it.copy(signInSuccess = false, signInError = null) }
    }
}