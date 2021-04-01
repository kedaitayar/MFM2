package io.github.kedaitayar.mfm.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.util.SnackbarEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val mainEventChannel = Channel<MainEvent>()
    val mainEvent = mainEventChannel.receiveAsFlow()

    @Deprecated("to be remove")
    private val _snackbarText = MutableLiveData<SnackbarEvent<String>>()
    @Deprecated("to be remove")
    val snackbarText: LiveData<SnackbarEvent<String>> get() = _snackbarText

    @Deprecated("to be remove")
    fun setSnackbarText(text: String) {
        _snackbarText.value = SnackbarEvent(text)
    }

    fun showSnackbar(msg: String, length: Int) {
        viewModelScope.launch {
            mainEventChannel.send(MainEvent.ShowSnackbar(msg, length))
        }
    }

    sealed class MainEvent {
        data class ShowSnackbar(val msg: String, val length: Int) : MainEvent()
    }
}