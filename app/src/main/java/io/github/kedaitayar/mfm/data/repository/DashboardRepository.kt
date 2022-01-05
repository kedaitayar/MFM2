package io.github.kedaitayar.mfm.data.repository

import androidx.lifecycle.LiveData
import io.github.kedaitayar.mfm.data.dao.AccountDao
import io.github.kedaitayar.mfm.data.dao.BasicDao
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.podata.AccountListAdapterData
import io.github.kedaitayar.mfm.data.podata.AccountTransactionBudgetData
import io.github.kedaitayar.mfm.data.podata.AccountTransactionChartData
import io.github.kedaitayar.mfm.data.podata.BudgetedAndGoal
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepository @Inject constructor(
    private val accountDao: AccountDao,
    private val basicDao: BasicDao
) {

    suspend fun insert(account: Account): Long {
        return basicDao.insert(account)
    }

    suspend fun delete(account: Account): Int {
        return basicDao.delete(account)
    }

    suspend fun update(account: Account): Int {
        return basicDao.update(account)
    }

    suspend fun getAccountById(accountId: Long): Account {
        return accountDao.getAccountById(accountId)
    }

    fun getAccountListData(): LiveData<List<AccountListAdapterData>> {
        return accountDao.getAccountListData()
    }

    fun getTotalBudgetedAmount(): Flow<Double?> {
        return accountDao.getTotalBudgetedAmount()
    }

    fun getTotalIncome(): Flow<Double?> {
        return accountDao.getTotalIncome()
    }

    fun getMonthSpending(timeFrom: OffsetDateTime, timeTo: OffsetDateTime): Flow<Double?> {
        return accountDao.getMonthSpending(timeFrom, timeTo)
    }

    fun getMonthBudgeted(month: Int, year: Int): Flow<Double> {
        return accountDao.getMonthBudgeted(month, year)
    }

    fun getUncompletedBudget(month: Int, year: Int): Flow<BudgetedAndGoal> {
        return accountDao.getUncompletedBudget(month, year)
    }

    fun getThisMonthBudgetedAmount(): Flow<Float?> {
        return accountDao.getThisMonthBudgetedAmount()
    }

    fun getAccountTransactionBudget(
        accountId: Long,
        timeFrom: OffsetDateTime,
        timeTo: OffsetDateTime
    ): Flow<List<AccountTransactionBudgetData>> {
        return accountDao.getAccountTransactionBudget(accountId, timeFrom, timeTo)
    }

    fun getAccountTransactionChartData(
        accountId: Long,
        month: Int,
        year: Int
    ): Flow<List<AccountTransactionChartData>> {
        val timeFrom = OffsetDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0))
        val timeTo = timeFrom.plusMonths(1).minusNanos(1)
        val timeToPrevMonth = timeFrom.minusNanos(1)
        return accountDao.getAccountTransactionChartData(
            accountId,
            timeFrom,
            timeTo,
            timeToPrevMonth
        )
    }
}