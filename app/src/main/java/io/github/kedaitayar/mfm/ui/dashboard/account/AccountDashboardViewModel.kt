package io.github.kedaitayar.mfm.ui.dashboard.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.dao.BasicDao
import io.github.kedaitayar.mfm.data.podata.BudgetedAndGoal
import io.github.kedaitayar.mfm.data.repository.DashboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.OffsetDateTime
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class AccountDashboardViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository,
    private val basicDao: BasicDao
) : ViewModel() {
    private val now = OffsetDateTime.now()
    val thisMonthSpending: LiveData<Double> = getThisMonthSpendingData().asLiveData()
    val totalBudgetedAndGoal: LiveData<BudgetedAndGoal> = getUncompletedBudget().asLiveData()
    val nextMonthBudgeted: LiveData<Double> = getMonthBudgeted().asLiveData()
    private val totalIncome: Flow<Double> = dashboardRepository.getTotalIncome()
    private val totalBudgetedAmount: Flow<Double> = dashboardRepository.getTotalBudgetedAmount()
    private val notBudgetedAmountFlow = combine(totalIncome, totalBudgetedAmount) { totalIncome, totalBudgetedAmount ->
        totalIncome + totalBudgetedAmount
    }
    val notBudgetedAmount = notBudgetedAmountFlow.asLiveData()

    private fun getThisMonthSpendingData(): Flow<Double> {
        val timeFrom =
            OffsetDateTime.of(now.year, now.monthValue, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0))
        val timeTo = timeFrom.plusMonths(1).minusNanos(1)
        return dashboardRepository.getMonthSpending(timeFrom, timeTo)
    }

    private fun getMonthBudgeted(): Flow<Double> {
        val now = OffsetDateTime.now()
        return dashboardRepository.getMonthBudgeted(now.monthValue + 1, now.year)
    }

    private fun getUncompletedBudget(): Flow<BudgetedAndGoal> {
        val now = OffsetDateTime.now()
        return dashboardRepository.getUncompletedBudget(now.monthValue, now.year)
    }
}