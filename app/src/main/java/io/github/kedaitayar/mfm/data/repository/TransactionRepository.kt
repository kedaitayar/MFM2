package io.github.kedaitayar.mfm.data.repository

import androidx.lifecycle.LiveData
import io.github.kedaitayar.mfm.data.dao.BasicDao
import io.github.kedaitayar.mfm.data.dao.TransactionDao
import io.github.kedaitayar.mfm.data.entity.TransactionType
import io.github.kedaitayar.mfm.data.podata.TransactionListAdapterData
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.entity.Budget
import io.github.kedaitayar.mfm.data.entity.Transaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val basicDao: BasicDao
) {

    suspend fun insert(transaction: Transaction): Long {
        return basicDao.insert(transaction)
    }

    suspend fun delete(transaction: Transaction): Int {
        return basicDao.delete(transaction)
    }

    suspend fun update(transaction: Transaction): Int {
        return basicDao.update(transaction)
    }

    fun getAllTransaction(): LiveData<List<Transaction>> {
        return basicDao.getAllTransaction()
    }

    fun getAllAccount(): LiveData<List<Account>> {
        return basicDao.getAllAccount()
    }

    fun getAllBudget(): LiveData<List<Budget>> {
        return basicDao.getAllBudget()
    }

    fun getTransactionListData(): LiveData<List<TransactionListAdapterData>> {
        return transactionDao.getTransactionListData()
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

    fun getAllTransactionType(): LiveData<List<TransactionType>> {
        return basicDao.getAllTransactionType()
    }
}