package io.github.kedaitayar.mfm.data.repository

import androidx.lifecycle.LiveData
import io.github.kedaitayar.mfm.data.dao.TransactionDao
import io.github.kedaitayar.mfm.data.entity.TransactionType
import io.github.kedaitayar.mfm.data.podata.TransactionListAdapterData
import io.github.kedaitayar.mfm2.data.entity.Account
import io.github.kedaitayar.mfm2.data.entity.Budget
import io.github.kedaitayar.mfm2.data.entity.Transaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(private val transactionDao: TransactionDao) {

    suspend fun insert(transaction: Transaction): Long {
        return transactionDao.insert(transaction)
    }

    suspend fun delete(transaction: Transaction): Int {
        return transactionDao.delete(transaction)
    }

    suspend fun update(transaction: Transaction): Int {
        return transactionDao.update(transaction)
    }

    suspend fun deleteAll() {
        return transactionDao.deleteAll()
    }

    fun getAllTransaction(): LiveData<List<Transaction>> {
        return transactionDao.getAllTransaction()
    }

    fun getAllAccount(): LiveData<List<Account>> {
        return transactionDao.getAllAccount()
    }

    fun getAllBudget(): LiveData<List<Budget>> {
        return transactionDao.getAllBudget()
    }

    fun getTransactionListData(): LiveData<List<TransactionListAdapterData>> {
        return transactionDao.getTransactionListData()
    }

    // transactionType
    suspend fun insert(transactionType: TransactionType): Long {
        return transactionDao.insert(transactionType)
    }

    suspend fun delete(transactionType: TransactionType): Int {
        return transactionDao.delete(transactionType)
    }

    suspend fun update(transactionType: TransactionType): Int {
        return transactionDao.update(transactionType)
    }

    suspend fun deleteAllTransactionType() {
        return transactionDao.deleteAllTransactionType()
    }

    fun getAllTransactionType(): LiveData<List<TransactionType>> {
        return transactionDao.getAllTransactionType()
    }
}