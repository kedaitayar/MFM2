package io.github.kedaitayar.mfm.data.repository

import io.github.kedaitayar.mfm.data.dao.BasicDao
import io.github.kedaitayar.mfm.data.entity.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BasicRepository @Inject constructor(
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

    suspend fun insert(budget: Budget): Long {
        return basicDao.insert(budget)
    }

    suspend fun update(budget: Budget): Int {
        return basicDao.update(budget)
    }

    suspend fun delete(budget: Budget): Int {
        return basicDao.delete(budget)
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

    suspend fun insert(budgetTransaction: BudgetTransaction): Long {
        return basicDao.insert(budgetTransaction)
    }

    suspend fun update(budgetTransaction: BudgetTransaction): Int {
        return basicDao.update(budgetTransaction)
    }

    suspend fun delete(budgetTransaction: BudgetTransaction): Int {
        return basicDao.delete(budgetTransaction)
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