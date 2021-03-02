package io.github.kedaitayar.mfm.data.repository

import androidx.lifecycle.LiveData
import io.github.kedaitayar.mfm.data.dao.AccountDao
import io.github.kedaitayar.mfm.data.dao.BasicDao
import io.github.kedaitayar.mfm.data.podata.AccountListAdapterData
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.podata.AccountTransactionBudgetData
import io.github.kedaitayar.mfm.data.podata.BudgetedAndGoal
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(
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

    fun getTotalBudgetedAmount(): LiveData<Double> {
        return  accountDao.getTotalBudgetedAmount()
    }

    fun getTotalIncome(): LiveData<Double> {
        return accountDao.getTotalIncome()
    }

    fun getMonthSpending(timeFrom: OffsetDateTime, timeTo: OffsetDateTime): LiveData<Double> {
        return accountDao.getMonthSpending(timeFrom, timeTo)
    }

    fun getMonthBudgeted(month: Int, year: Int): LiveData<Double> {
        return accountDao.getMonthBudgeted(month, year)
    }

    fun getUncompletedBudget(month: Int, year: Int): LiveData<BudgetedAndGoal> {
        return accountDao.getUncompletedBudget(month, year)
    }

    fun getAccountTransactionBudget(accountId: Long, timeFrom: OffsetDateTime, timeTo: OffsetDateTime): List<AccountTransactionBudgetData> {
        return accountDao.getAccountTransactionBudget(accountId, timeFrom, timeTo)
    }
}