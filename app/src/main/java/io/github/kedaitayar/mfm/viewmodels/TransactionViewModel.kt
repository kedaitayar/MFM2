package io.github.kedaitayar.mfm.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.entity.TransactionType
import io.github.kedaitayar.mfm.data.podata.TransactionListAdapterData
import io.github.kedaitayar.mfm.data.repository.TransactionRepository
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.entity.Budget
import io.github.kedaitayar.mfm.data.entity.Transaction
import io.github.kedaitayar.mfm.data.podata.TransactionGraphData
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
//    val allTransaction: LiveData<List<Transaction>> = transactionRepository.getAllTransaction()
    val allAccount: LiveData<List<Account>> = transactionRepository.getAllAccount()
    val allBudget: LiveData<List<Budget>> = transactionRepository.getAllBudget()
    val allTransactionListAdapterData: LiveData<List<TransactionListAdapterData>> = transactionRepository.getTransactionListData()
    val transactionYearlyTrendGraph: LiveData<List<TransactionGraphData>> = transactionRepository.getTransactionGraphData()

    suspend fun insert(transaction: Transaction): Long {
        return transactionRepository.insert(transaction)
    }

    suspend fun delete(transaction: Transaction): Int {
        return transactionRepository.delete(transaction)
    }

    suspend fun update(transaction: Transaction): Int {
        return transactionRepository.update(transaction)
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

    fun getAllTransactionType(): LiveData<List<TransactionType>> {
        return transactionRepository.getAllTransactionType()
    }

    suspend fun getTransactionById(transactionId: Long) : Transaction {
        return transactionRepository.getTransactionById(transactionId)
    }
}