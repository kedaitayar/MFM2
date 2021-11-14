package io.github.kedaitayar.mfm.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.github.kedaitayar.mfm.data.dao.AccountDao
import io.github.kedaitayar.mfm.data.dao.BasicDao
import io.github.kedaitayar.mfm.data.dao.BudgetDao
import io.github.kedaitayar.mfm.data.dao.TransactionDao
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.entity.Budget
import io.github.kedaitayar.mfm.data.entity.Transaction
import io.github.kedaitayar.mfm.data.entity.TransactionType
import io.github.kedaitayar.mfm.data.podata.AccountListAdapterData
import io.github.kedaitayar.mfm.data.podata.BudgetListAdapterData
import io.github.kedaitayar.mfm.data.podata.TransactionGraphData
import io.github.kedaitayar.mfm.data.podata.TransactionListAdapterData
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val accountDao: AccountDao,
    private val budgetDao: BudgetDao,
    private val basicDao: BasicDao
) {

    fun getTransactionListData(): Flow<PagingData<TransactionListAdapterData>> {
        return Pager(
            config = PagingConfig(pageSize = 30, enablePlaceholders = false),
            pagingSourceFactory = { transactionDao.getTransactionListData() }
        ).flow
    }

    fun getBudgetMonthlyListAdapterFlow(
        month: Int,
        year: Int,
    ): Flow<List<BudgetListAdapterData>> {
        val timeFrom = OffsetDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0))
        val timeTo = timeFrom.plusMonths(1).minusNanos(1)
        return budgetDao.getBudgetMonthlyListAdapterFlow(month, year, timeFrom, timeTo)
    }

    fun getBudgetYearlyListAdapterFlow(
        month: Int,
        year: Int,
    ): Flow<List<BudgetListAdapterData>> {
        val timeFrom = OffsetDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0))
        val timeTo = timeFrom.plusMonths(1).minusNanos(1)
        return budgetDao.getBudgetYearlyListAdapterFlow(month, year, timeFrom, timeTo)
    }

    fun getAccountListDataFlow(): Flow<List<AccountListAdapterData>> {
        return accountDao.getAccountListDataFlow()
    }

    suspend fun insert(transaction: Transaction): Long {
        return basicDao.insert(transaction)
    }

    suspend fun delete(transaction: Transaction): Int {
        return basicDao.delete(transaction)
    }

    suspend fun update(transaction: Transaction): Int {
        return basicDao.update(transaction)
    }

    suspend fun getTransactionById(transactionId: Long): Transaction {
        return basicDao.getTransactionById(transactionId)
    }

    fun getAllBudget(): LiveData<List<Budget>> {
        return basicDao.getAllBudget()
    }

    fun getAllAccountFlow(): Flow<List<Account>> {
        return basicDao.getAllAccountFlow()
    }

    fun getAllBudgetFlow(): Flow<List<Budget>> {
        return basicDao.getAllBudgetFlow()
    }

    fun getAccountByIdFlow(accountId: Long): Flow<Account> {
        return basicDao.getAccountByIdFlow(accountId)
    }

    fun getBudgetByIdFlow(budgetId: Long): Flow<Budget> {
        return basicDao.getBudgetByIdFlow(budgetId)
    }

    fun getTransactionGraphData(): Flow<List<TransactionGraphData>> {
        return transactionDao.getTransactionGraphData()
    }

    // transactionType
    suspend fun insert(transactionType: TransactionType): Long {
        return basicDao.insert(transactionType)
    }

    suspend fun delete(transactionType: TransactionType): Int {
        return basicDao.delete(transactionType)
    }

    suspend fun update(transactionType: TransactionType): Int {
        return basicDao.update(transactionType)
    }

}