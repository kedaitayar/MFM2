package io.github.kedaitayar.mfm.data.repository

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.kedaitayar.mfm.data.dao.BudgetDao
import io.github.kedaitayar.mfm2.data.entity.Budget
import io.github.kedaitayar.mfm2.data.entity.BudgetType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepository @Inject constructor(private val budgetDao: BudgetDao) {

    suspend fun insert(budget: Budget): Long {
        return budgetDao.insert(budget)
    }

    suspend fun update(budget: Budget): Int {
        return budgetDao.update(budget)
    }

    suspend fun delete(budget: Budget): Int {
        return budgetDao.delete(budget)
    }

    fun getAllBudget(): LiveData<List<Budget>> {
        return budgetDao.getAllBudget()
    }


    //budgetType
    fun getAllBudgetTypeLV(): LiveData<List<BudgetType>> {
        return budgetDao.getAllBudgetTypeLV()
    }

    suspend fun insert(budgetType: BudgetType): Long {
        return budgetDao.insert(budgetType)
    }

    suspend fun update(budgetType: BudgetType): Int {
        return budgetDao.update(budgetType)
    }

    suspend fun delete(budgetType: BudgetType): Int {
        return budgetDao.delete(budgetType)
    }

    suspend fun deleteAllBudgetType() {
        budgetDao.deleteAll()
    }

    suspend fun getAllBudgetType(): List<BudgetType>{
        return budgetDao.getAllBudgetType()
    }
}