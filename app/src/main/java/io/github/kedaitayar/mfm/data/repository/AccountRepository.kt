package io.github.kedaitayar.mfm.data.repository

import androidx.lifecycle.LiveData
import io.github.kedaitayar.mfm.data.dao.AccountDao
import io.github.kedaitayar.mfm.data.podata.AccountListAdapterData
import io.github.kedaitayar.mfm2.data.entity.Account
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(private val accountDao: AccountDao) {

    suspend fun insert(account: Account): Long {
        return accountDao.insert(account)
    }

    suspend fun delete(account: Account): Int {
        return accountDao.delete(account)
    }

    suspend fun update(account: Account): Int {
        return accountDao.update(account)
    }

    suspend fun getAccountById(accountId: Long): Account {
        return accountDao.getAccountById(accountId)
    }

    fun getAccountListData(): LiveData<List<AccountListAdapterData>> {
        return accountDao.getAccountListData()
    }
}