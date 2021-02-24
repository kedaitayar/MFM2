package io.github.kedaitayar.mfm.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import io.github.kedaitayar.mfm.data.dao.BasicDao
import io.github.kedaitayar.mfm.data.dao.BudgetDao
import io.github.kedaitayar.mfm.data.entity.Budget
import io.github.kedaitayar.mfm.data.entity.BudgetTransaction
import io.github.kedaitayar.mfm.data.entity.BudgetType
import io.github.kedaitayar.mfm.data.podata.BudgetListAdapterData
import java.time.OffsetDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "BudgetRepository"

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


    //budgetType
    fun getAllBudgetType(): LiveData<List<BudgetType>> {
        return basicDao.getAllBudgetType()
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


    fun getBudgetMonthlyListAdapterDO(
        month: Int,
        year: Int
    ): LiveData<List<BudgetListAdapterData>> {
        val timeFrom = OffsetDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0))
        val timeTo = timeFrom.plusMonths(1).minusNanos(1)
        Log.i(TAG, "getBudgetMonthlyListAdapterDO timeFrom: $timeFrom")
        Log.i(TAG, "getBudgetMonthlyListAdapterDO timeTo: $timeTo")
        return budgetDao.getBudgetMonthlyListAdapterDO(month, year, timeFrom, timeTo)
    }

    fun getBudgetingListAdapterDO(
        month: Int,
        year: Int
    ): LiveData<List<BudgetListAdapterData>> {
        val timeFrom = OffsetDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0))
        val timeTo = timeFrom.plusMonths(1).minusNanos(1)
        Log.i(TAG, "getBudgetingListAdapterDO timeFrom: $timeFrom")
        Log.i(TAG, "getBudgetingListAdapterDO timeTo: $timeTo")
        return budgetDao.getBudgetingListAdapterDO(month, year, timeFrom, timeTo)
    }
}