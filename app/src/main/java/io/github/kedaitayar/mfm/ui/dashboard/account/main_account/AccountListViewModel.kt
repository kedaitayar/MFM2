package io.github.kedaitayar.mfm.ui.dashboard.account.main_account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.repository.DashboardRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountListViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository
) : ViewModel() {
    private val accountListEventChannel = Channel<AccountListEvent>()
    val accountListEvent = accountListEventChannel.receiveAsFlow()

    val accountListAdapterData = dashboardRepository.getAccountListData()

    fun onAccountDetailClick(account: Account) {
        viewModelScope.launch {
            accountListEventChannel.send(AccountListEvent.NavigateToAccountDetail(account))
        }
    }

    fun onEditAccountClick(account: Account) {
        viewModelScope.launch {
            accountListEventChannel.send(AccountListEvent.NavigateToEditAccount(account))
        }
    }

    sealed class AccountListEvent {
        data class NavigateToAccountDetail(val account: Account) : AccountListEvent()
        data class NavigateToEditAccount(val account: Account) : AccountListEvent()
    }
}