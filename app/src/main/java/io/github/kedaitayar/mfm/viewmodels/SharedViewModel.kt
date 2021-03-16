package io.github.kedaitayar.mfm.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.util.SnackbarEvent
import javax.inject.Inject

private const val TAG = "SharedViewModel"

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {
    private val _snackbarText = MutableLiveData<SnackbarEvent<String>>()
    val snackbarText: LiveData<SnackbarEvent<String>> get() = _snackbarText

    fun setSnackbarText(text: String) {
        _snackbarText.value = SnackbarEvent(text)
    }


    init {
        Log.d(TAG, "SharedViewModel has created!")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "SharedViewModel has removed!")
    }
}