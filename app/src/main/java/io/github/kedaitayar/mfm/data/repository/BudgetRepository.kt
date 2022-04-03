package io.github.kedaitayar.mfm.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import io.github.kedaitayar.mfm.data.dao.BasicDao
import io.github.kedaitayar.mfm.data.dao.BudgetDao
import io.github.kedaitayar.mfm.data.entity.Budget
import io.github.kedaitayar.mfm.data.entity.BudgetPosition
import io.github.kedaitayar.mfm.data.entity.BudgetTransaction
import io.github.kedaitayar.mfm.data.entity.BudgetType
import io.github.kedaitayar.mfm.data.podata.*
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepository @Inject constructor(
    private val budgetDao: BudgetDao,
    private val basicDao: BasicDao
) {

    suspend fun insert(budget: Budget): Long {
        return basicDao.insert(budget)
    }

    suspend fun update(budget: Budget): Int {
        return basicDao.update(budget)
    }

    suspend fun delete(budget: Budget): Int {
        return basicDao.delete(budget)
    }

    fun getAllBudget(): LiveData<List<Budget>> {
        return basicDao.getAllBudget()
    }

    fun getAllBudgetFlow(): Flow<List<Budget>> {
        return basicDao.getAllBudgetFlow()
    }

    suspend fun updatePosition(budgetPosition: BudgetPosition): Int {
        return basicDao.updatePosition(budgetPosition)
    }

    suspend fun updatePosition(budgetPositionList: List<BudgetPosition>): Int {
        return basicDao.updatePosition(budgetPositionList)
    }


    //budgetType
    fun getAllBudgetType(): LiveData<List<BudgetType>> {
        return basicDao.getAllBudgetType()
    }

    fun getAllBudgetTypeFlow(): Flow<List<BudgetType>> {
        return basicDao.getAllBudgetTypeFlow()
    }

    suspend fun insert(budgetType: BudgetType): Long {
        return basicDao.insert(budgetType)
    }

    suspend fun update(budgetType: BudgetType): Int {
        return basicDao.update(budgetType)
    }

    suspend fun delete(budgetType: BudgetType): Int {
        return basicDao.delete(budgetType)
    }

    //budgetTransaction
    fun getAllBudgetTransaction(): LiveData<List<BudgetTransaction>> {
        return basicDao.getAllBudgetTransaction()
    }

    suspend fun insert(budgetTransaction: BudgetTransaction): Long {
        return basicDao.insert(budgetTransaction)
    }

    suspend fun update(budgetTransaction: BudgetTransaction): Int {
        return basicDao.update(budgetTransaction)
    }

    suspend fun delete(budgetTransaction: BudgetTransaction): Int {
        return basicDao.delete(budgetTransaction)
    }


    fun getBudgetMonthlyListAdapter(month: Int, year: Int): LiveData<List<BudgetListAdapterData>> {
        val timeFrom = OffsetDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0))
        val timeTo = timeFrom.plusMonths(1).minusNanos(1)
        return budgetDao.getBudgetMonthlyListAdapter(month, year, timeFrom, timeTo)
    }

    fun getBudgetYearlyListAdapter(month: Int, year: Int): LiveData<List<BudgetListAdapterData>> {
        val timeFrom = OffsetDateTime.of(year, 1, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0))
        val timeTo = timeFrom.plusMonths(month.toLong()).minusNanos(1)
        return budgetDao.getBudgetYearlyListAdapter(month, year, timeFrom, timeTo)
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
        val timeFrom = OffsetDateTime.of(year, 1, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0))
        val timeTo = timeFrom.plusYears(1).minusNanos(1)
        return budgetDao.getBudgetYearlyListAdapterFlow(month, year, timeFrom, timeTo)
    }

    fun getBudgetingListAdapterDO(
        month: Int,
        year: Int
    ): LiveData<List<BudgetListAdapterData>> {
        val timeFrom = OffsetDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0))
        val timeTo = timeFrom.plusMonths(1).minusNanos(1)
        return budgetDao.getBudgetingListAdapterDO(month, year, timeFrom, timeTo)
    }

    fun getMonthlyBudgetingListAdapterDO(
        month: Int,
        year: Int
    ): LiveData<List<BudgetListAdapterData>> {
        val timeFrom = OffsetDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0))
        val timeTo = timeFrom.plusMonths(1).minusNanos(1)
        return budgetDao.getMonthlyBudgetingListAdapterDO(month, year, timeFrom, timeTo)
    }

    fun getYearlyBudgetingListAdapterDO(
        month: Int,
        year: Int
    ): LiveData<List<BudgetListAdapterData>> {
        val timeFrom = OffsetDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0))
        val timeTo = timeFrom.plusMonths(1).minusNanos(1)
        return budgetDao.getYearlyBudgetingListAdapterDO(month, year, timeFrom, timeTo)
    }

    fun getMonthlyBudgetingListAdapterFlow(
        month: Int,
        year: Int
    ): Flow<List<BudgetListAdapterData>> {
        val timeFrom = OffsetDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0))
        val timeTo = timeFrom.plusMonths(1).minusNanos(1)
        return budgetDao.getMonthlyBudgetingListAdapterFlow(month, year, timeFrom, timeTo)
    }

    fun getYearlyBudgetingListAdapterFlow(
        month: Int,
        year: Int
    ): Flow<List<BudgetListAdapterData>> {
        val timeFrom = OffsetDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0))
        val timeTo = timeFrom.plusMonths(1).minusNanos(1)
        return budgetDao.getYearlyBudgetingListAdapterFlow(month, year, timeFrom, timeTo)
    }

    fun getTotalBudgetedAmount(): Flow<Double> {
        return budgetDao.getTotalBudgetedAmount()
    }

    fun getTotalIncome(): Flow<Double> {
        return budgetDao.getTotalIncome()
    }

    fun getBudgetTransactionJoinTransaction(): LiveData<List<BudgetTransactionJoinTransaction>> {
        return budgetDao.getBudgetTransactionJoinTransaction()
    }

    fun getBudgetTransactionJoinTransactionFlow(): Flow<List<BudgetTransactionJoinTransaction>> {
        return budgetDao.getBudgetTransactionJoinTransactionFlow()
    }

    suspend fun getBudgetTransactionJoinTransactionSuspend(): List<BudgetTransactionJoinTransaction> {
        return budgetDao.getBudgetTransactionJoinTransactionSuspend()
    }

    fun getBudgetTransactionAmountList(): Flow<List<BudgetTransactionAmountList>> {
        return budgetDao.getBudgetTransactionAmountList()
    }

    fun getBudgetTransactionAmountList(
        timeFrom: OffsetDateTime,
        timeTo: OffsetDateTime
    ): Flow<List<BudgetTransactionAmountList>> {
        return budgetDao.getBudgetTransactionAmountList(timeFrom, timeTo)
    }

    fun getMonthlySpendingGraphDataByBudget(budgetId: Long): Flow<List<MonthlySpendingData>> {
        return budgetDao.getMonthlySpendingGraphDataByBudget(budgetId)
    }

    fun getTransactionListByBudgetData(budgetId: Long): Flow<PagingData<TransactionListAdapterData>> {
        return Pager(config = PagingConfig(pageSize = 30, enablePlaceholders = false),
            pagingSourceFactory = { budgetDao.getTransactionListByBudgetData(budgetId) }).flow
    }
}