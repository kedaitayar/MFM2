package io.github.kedaitayar.mfm

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.entity.*
import io.github.kedaitayar.mfm.data.repository.BasicRepository
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel
@Inject constructor(
    private val basicRepository: BasicRepository
) : ViewModel() {
    suspend fun insert(account: Account): Long {
        return basicRepository.insert(account)
    }

    suspend fun delete(account: Account): Int {
        return basicRepository.delete(account)
    }

    suspend fun update(account: Account): Int {
        return basicRepository.update(account)
    }

    suspend fun insert(budget: Budget): Long {
        return basicRepository.insert(budget)
    }

    suspend fun update(budget: Budget): Int {
        return basicRepository.update(budget)
    }

    suspend fun delete(budget: Budget): Int {
        return basicRepository.delete(budget)
    }

    suspend fun insert(budgetType: BudgetType): Long {
        return basicRepository.insert(budgetType)
    }

    suspend fun update(budgetType: BudgetType): Int {
        return basicRepository.update(budgetType)
    }

    suspend fun delete(budgetType: BudgetType): Int {
        return basicRepository.delete(budgetType)
    }

    suspend fun insert(budgetTransaction: BudgetTransaction): Long {
        return basicRepository.insert(budgetTransaction)
    }

    suspend fun update(budgetTransaction: BudgetTransaction): Int {
        return basicRepository.update(budgetTransaction)
    }

    suspend fun delete(budgetTransaction: BudgetTransaction): Int {
        return basicRepository.delete(budgetTransaction)
    }

    suspend fun insert(transaction: Transaction): Long {
        return basicRepository.insert(transaction)
    }

    suspend fun delete(transaction: Transaction): Int {
        return basicRepository.delete(transaction)
    }

    suspend fun update(transaction: Transaction): Int {
        return basicRepository.update(transaction)
    }

    suspend fun insert(transactionType: TransactionType): Long {
        return basicRepository.insert(transactionType)
    }

    suspend fun delete(transactionType: TransactionType): Int {
        return basicRepository.delete(transactionType)
    }

    suspend fun update(transactionType: TransactionType): Int {
        return basicRepository.update(transactionType)
    }

    suspend fun insert(quickTransaction: QuickTransaction): Long {
        return basicRepository.insert(quickTransaction)
    }

    suspend fun delete(quickTransaction: QuickTransaction): Int {
        return basicRepository.delete(quickTransaction)
    }

    suspend fun update(quickTransaction: QuickTransaction): Int {
        return basicRepository.delete(quickTransaction)
    }
}