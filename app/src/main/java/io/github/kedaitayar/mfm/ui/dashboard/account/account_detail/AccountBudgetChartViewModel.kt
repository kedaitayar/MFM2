package io.github.kedaitayar.mfm.ui.dashboard.account.account_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.PieEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.podata.AccountTransactionBudgetData
import io.github.kedaitayar.mfm.data.repository.DashboardRepository
import io.github.kedaitayar.mfm.data.repository.SelectedDateRepository
import kotlinx.coroutines.flow.*
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class AccountBudgetChartViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository,
    private val selectedDateRepository: SelectedDateRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val selectedOffsetDate = selectedDateRepository.selectedOffsetDate
    var account = MutableStateFlow(
        savedStateHandle.get<Account>(AccountDetailFragment.ACCOUNT_STATE_KEY) ?: Account(accountId = -1)
    )
        set(value) {
            field = value
            savedStateHandle.set(AccountDetailFragment.ACCOUNT_STATE_KEY, value.value)
        }

    val accountTransactionBudgetFlow: Flow<List<AccountTransactionBudgetData>> =
        account.combine(selectedOffsetDate) { account, date ->
//            getAccountTransactionBudget(account.accountId, date.atZone(ZoneId.systemDefault()).toOffsetDateTime())
            getAccountTransactionBudget(account.accountId, date)
        }.flattenMerge()

    val pieEntries: Flow<List<PieEntry>> = accountTransactionBudgetFlow.flatMapLatest {
        flow {
            val tempPieEntry = mutableListOf<PieEntry>()
            for (item in it) {
                tempPieEntry.add(PieEntry(item.transactionAmount ?: 0.0f, item.budgetName))
            }
            emit(tempPieEntry)
        }
    }

    val totalTransactionAmount = accountTransactionBudgetFlow.mapLatest { list ->
        list.sumOf { item ->
            item.transactionAmount?.toDouble() ?: 0.0
        }
    }

    private fun getAccountTransactionBudget(
        accountId: Long,
        dateTime: OffsetDateTime
    ): Flow<List<AccountTransactionBudgetData>> {
        val timeTo = dateTime.plusMonths(1).minusNanos(1)
        return dashboardRepository.getAccountTransactionBudget(accountId, dateTime, timeTo)
    }
}