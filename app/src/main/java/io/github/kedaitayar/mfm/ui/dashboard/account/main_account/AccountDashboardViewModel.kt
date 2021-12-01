package io.github.kedaitayar.mfm.ui.dashboard.account.main_account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.podata.BudgetedAndGoal
import io.github.kedaitayar.mfm.data.repository.DashboardRepository
import io.github.kedaitayar.mfm.util.notNull
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class AccountDashboardViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository
) : ViewModel() {
    private val now = OffsetDateTime.now()
    val thisMonthSpending = getThisMonthSpendingData()
    val totalBudgetedAndGoal = getUncompletedBudget()
    val nextMonthBudgeted = getMonthBudgeted()
    val thisMonthBudgeted = dashboardRepository.getThisMonthBudgetedAmount()
    private val totalIncome = dashboardRepository.getTotalIncome()
    private val totalBudgetedAmount = dashboardRepository.getTotalBudgetedAmount()
    val notBudgetedAmount =
        combine(totalIncome, totalBudgetedAmount) { totalIncome, totalBudgetedAmount ->
            totalIncome.notNull() - totalBudgetedAmount.notNull()
        }
    private val accountDashboardEventChannel = Channel<AccountDashboardEvent>()
    val accountDashboardEvent = accountDashboardEventChannel.receiveAsFlow()

    private fun getThisMonthSpendingData(): Flow<Double?> {
        val timeFrom =
            OffsetDateTime.of(now.year, now.monthValue, 1, 0, 0, 0, 0, OffsetDateTime.now().offset)
        val timeTo = timeFrom.plusMonths(1).minusNanos(1)
        return dashboardRepository.getMonthSpending(timeFrom, timeTo)
    }

    private fun getMonthBudgeted(): Flow<Double?> {
        val nextMonth = OffsetDateTime.now().plusMonths(1)
        return dashboardRepository.getMonthBudgeted(nextMonth.monthValue, nextMonth.year)
    }

    private fun getUncompletedBudget(): Flow<BudgetedAndGoal> {
        val now = OffsetDateTime.now()
        return dashboardRepository.getUncompletedBudget(now.monthValue, now.year)
    }

    fun onAddNewAccountClick() {
        viewModelScope.launch {
            accountDashboardEventChannel.send(AccountDashboardEvent.NavigateToAddAccount)
        }
    }

    sealed class AccountDashboardEvent {
        object NavigateToAddAccount : AccountDashboardEvent()
    }
}