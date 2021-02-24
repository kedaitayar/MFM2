package io.github.kedaitayar.mfm.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.podata.AccountListAdapterData
import io.github.kedaitayar.mfm.data.repository.AccountRepository
import io.github.kedaitayar.mfm.data.entity.Account
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {
    val accountListAdapterData: LiveData<List<AccountListAdapterData>> =
        accountRepository.getAccountListData()
    val totalBudgetedAmount: LiveData<Double> = accountRepository.getTotalBudgetedAmount()
    val totalIncome: LiveData<Double> = accountRepository.getTotalIncome()

    suspend fun insert(account: Account): Long {
        return accountRepository.insert(account)
    }

    suspend fun update(account: Account): Int {
        return accountRepository.update(account)
    }

    suspend fun delete(account: Account): Int {
        return accountRepository.delete(account)
    }

    suspend fun getAccountById(accountId: Long): Account {
        return accountRepository.getAccountById(accountId)
    }
}