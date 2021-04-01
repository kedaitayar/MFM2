package io.github.kedaitayar.mfm.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.github.kedaitayar.mfm.data.dao.BasicDao
import io.github.kedaitayar.mfm.data.dao.TransactionDao
import io.github.kedaitayar.mfm.data.entity.TransactionType
import io.github.kedaitayar.mfm.data.podata.TransactionListAdapterData
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.entity.Budget
import io.github.kedaitayar.mfm.data.entity.Transaction
import io.github.kedaitayar.mfm.data.podata.TransactionGraphData
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val basicDao: BasicDao
) {

    fun getTransactionListData(): Flow<PagingData<TransactionListAdapterData>> {
        return Pager(
            config = PagingConfig(pageSize = 30, enablePlaceholders = false),
            pagingSourceFactory = {transactionDao.getTransactionListData()}
        ).flow
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

    suspend fun getTransactionById(transactionId: Long) : Transaction {
        return basicDao.getTransactionById(transactionId)
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

    fun getTransactionGraphData(year: String): Flow<List<TransactionGraphData>> {
        return transactionDao.getTransactionGraphData(year)
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