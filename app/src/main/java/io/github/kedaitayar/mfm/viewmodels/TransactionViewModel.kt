package io.github.kedaitayar.mfm.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.entity.TransactionType
import io.github.kedaitayar.mfm.data.podata.TransactionListAdapterData
import io.github.kedaitayar.mfm.data.repository.TransactionRepository
import io.github.kedaitayar.mfm2.data.entity.Account
import io.github.kedaitayar.mfm2.data.entity.Budget
import io.github.kedaitayar.mfm2.data.entity.BudgetType
import io.github.kedaitayar.mfm2.data.entity.Transaction
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
//    val allTransaction: LiveData<List<Transaction>> = transactionRepository.getAllTransaction()
    val allAccount: LiveData<List<Account>> = transactionRepository.getAllAccount()
    val allBudget: LiveData<List<Budget>> = transactionRepository.getAllBudget()
    val allTransactionListAdapterData: LiveData<List<TransactionListAdapterData>> = transactionRepository.getTransactionListData()

    suspend fun insert(transaction: Transaction): Long {
        return transactionRepository.insert(transaction)
    }

    suspend fun delete(transaction: Transaction): Int {
        return transactionRepository.delete(transaction)
    }

    suspend fun update(transaction: Transaction): Int {
        return transactionRepository.update(transaction)
    }

    suspend fun deleteAll() {
        return transactionRepository.deleteAll()
    }

    // transactionType
    suspend fun insert(transactionType: TransactionType): Long {
        return transactionRepository.insert(transactionType)
    }

    suspend fun delete(transactionType: TransactionType): Int {
        return transactionRepository.delete(transactionType)
    }

    suspend fun update(transactionType: TransactionType): Int {
        return transactionRepository.update(transactionType)
    }

    suspend fun deleteAllTransactionType() {
        return transactionRepository.deleteAllTransactionType()
    }

    fun getAllTransactionType(): LiveData<List<TransactionType>> {
        return transactionRepository.getAllTransactionType()
    }
}