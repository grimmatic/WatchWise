package com.vakifbank.WatchWise.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

open class BaseViewModel : ViewModel() {
    protected val _state = MutableStateFlow<UIState>(UIState.Init)
    val state: StateFlow<UIState> get() = _state

    protected fun showLoading() {
        _state.value = UIState.IsLoading(true)
    }

    protected fun hideLoading() {
        _state.value = UIState.IsLoading(false)
    }

    protected fun showToast(message: String) {
        _state.value = UIState.ShowToast(message)
    }

    protected fun showErrorMessage(message: String) {
        _state.value = UIState.Error(message)
    }

    fun resetState() {
        _state.value = UIState.Init
    }
}

sealed class UIState {
    object Init : UIState()
    data class IsLoading(val isLoading: Boolean) : UIState()
    data class ShowToast(val message: String) : UIState()
    data class Error(val rawResponse: String) : UIState()
}