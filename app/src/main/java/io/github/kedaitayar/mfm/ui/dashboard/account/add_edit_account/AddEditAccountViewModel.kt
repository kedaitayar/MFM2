package io.github.kedaitayar.mfm.ui.dashboard.account.add_edit_account

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.repository.DashboardRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditAccountViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository,
    private val state: SavedStateHandle
) : ViewModel() {
    var account = state.get<Account>("account") ?: Account()

    private val addEditAccountEventChannel = Channel<AddEditAccountEvent>()
    val addEditAccountEvent = addEditAccountEventChannel.receiveAsFlow()

    fun onSaveClick() {
        viewModelScope.launch {
            if (account?.accountName?.isNotBlank() == true) {
                val result = dashboardRepository.update(Account(accountId = account.accountId, accountName = account.accountName))
                addEditAccountEventChannel.send(AddEditAccountEvent.NavigateBackWithEditResult(result))
            } else {
                addEditAccountEventChannel.send(
                    AddEditAccountEvent.ShowSnackbar(
                        "Account name cannot be empty",
                        Snackbar.LENGTH_SHORT
                    )
                )
            }
        }
    }

    fun onAddClick() {
        viewModelScope.launch {
            if (account?.accountName?.isBlank() == true) {
                val result = dashboardRepository.insert(Account(accountId = account.accountId, accountName = account.accountName))
                addEditAccountEventChannel.send(AddEditAccountEvent.NavigateBackWithAddResult(result))
            } else {
                addEditAccountEventChannel.send(
                    AddEditAccountEvent.ShowSnackbar(
                        "Account name cannot be empty",
                        Snackbar.LENGTH_SHORT
                    )
                )
            }
        }
    }

    fun onDeleteClick() {
        viewModelScope.launch {
            val result = dashboardRepository.delete(Account(accountId = account!!.accountId, accountName = account.accountName))
            addEditAccountEventChannel.send(AddEditAccountEvent.NavigateBackWithDeleteResult(result))
        }
    }

    sealed class AddEditAccountEvent {
        data class ShowSnackbar(val msg: String, val length: Int) : AddEditAccountEvent()
        data class NavigateBackWithAddResult(val result: Long) : AddEditAccountEvent()
        data class NavigateBackWithEditResult(val result: Int) : AddEditAccountEvent()
        data class NavigateBackWithDeleteResult(val result: Int) : AddEditAccountEvent()
    }
}