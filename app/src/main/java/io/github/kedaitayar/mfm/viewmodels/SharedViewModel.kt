package io.github.kedaitayar.mfm.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.util.SnackbarEvent
import javax.inject.Inject


@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {
    private val _snackbarText = MutableLiveData<SnackbarEvent<String>>()
    val snackbarText: LiveData<SnackbarEvent<String>> get() = _snackbarText

    fun setSnackbarText(text: String) {
        _snackbarText.value = SnackbarEvent(text)
    }
}