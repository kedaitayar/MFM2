package io.github.kedaitayar.mfm.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.entity.QuickTransaction
import io.github.kedaitayar.mfm.data.repository.QuickTransactionRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val quickTransactionRepository: QuickTransactionRepository) :
    ViewModel() {
    private val mainEventChannel = Channel<MainEvent>()
    val mainEvent = mainEventChannel.receiveAsFlow()

    fun getAllQuickTransaction(): Flow<List<QuickTransaction>> {
        return quickTransactionRepository.getAllQuickTransaction()
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