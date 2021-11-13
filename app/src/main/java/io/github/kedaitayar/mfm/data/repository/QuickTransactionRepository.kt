package io.github.kedaitayar.mfm.data.repository

import io.github.kedaitayar.mfm.data.dao.BasicDao
import io.github.kedaitayar.mfm.data.dao.QuickTransactionDao
import io.github.kedaitayar.mfm.data.entity.QuickTransaction
import io.github.kedaitayar.mfm.data.podata.QuickTransactionListAdapterData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuickTransactionRepository @Inject constructor(private val basicDao: BasicDao, private val quickTransactionDao: QuickTransactionDao) {
    fun getAllQuickTransaction(): Flow<List<QuickTransaction>> {
        return basicDao.getAllQuickTransaction()
    }
    fun getQuickTransactionList(): Flow<List<QuickTransactionListAdapterData>> {
        return quickTransactionDao.getQuickTransactionList()
    }
}