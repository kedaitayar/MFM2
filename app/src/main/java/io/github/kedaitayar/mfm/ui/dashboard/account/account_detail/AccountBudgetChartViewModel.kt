package io.github.kedaitayar.mfm.ui.dashboard.account.account_detail

import androidx.lifecycle.*
import com.github.mikephil.charting.data.PieEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.podata.AccountTransactionBudgetData
import io.github.kedaitayar.mfm.data.repository.DashboardRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class AccountBudgetChartViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var account = MutableStateFlow(savedStateHandle.get<Account>(AccountDetailFragment.ACCOUNT_STATE_KEY) ?: Account(accountId = -1))
        set(value) {
            field = value
            savedStateHandle.set(AccountDetailFragment.ACCOUNT_STATE_KEY, value.value)
        }

    private val accountTransactionBudgetFlow = account.flatMapLatest {
        getAccountTransactionBudget(it.accountId!!)
    }
    val accountTransactionBudget = accountTransactionBudgetFlow.asLiveData()

    val totalTransactionAmount = accountTransactionBudgetFlow.map { list ->
        list.sumByDouble { item ->
            item.transactionAmount?.toDouble() ?: 0.0
        }
    }.asLiveData()

    val pieEntries = MutableLiveData<List<PieEntry>>()

    init {
        setupAccountBudgetChartData()
    }

    private fun setupAccountBudgetChartData() {
        viewModelScope.launch {
            accountTransactionBudgetFlow.collect { list ->
                val tempPieEntry = mutableListOf<PieEntry>()
                tempPieEntry.clear()
                for (item in list) {
                    val transactionAmount = item.transactionAmount ?: 0.0f
                    val pieEntry = PieEntry(transactionAmount, item.budgetName)
                    tempPieEntry.add(pieEntry)
//                    pieEntries.postValue(tempPieEntry)
                    pieEntries.value = tempPieEntry
                }
            }
        }
    }

    private fun getAccountTransactionBudget(accountId: Long): Flow<List<AccountTransactionBudgetData>> {
        val now = OffsetDateTime.now()
        val timeFrom =
            OffsetDateTime.of(now.year, now.monthValue, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0))
        val timeTo = timeFrom.plusMonths(1).minusNanos(1)
        return dashboardRepository.getAccountTransactionBudget(accountId, timeFrom, timeTo)
    }
}