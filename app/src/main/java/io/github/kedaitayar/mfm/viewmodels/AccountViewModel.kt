package io.github.kedaitayar.mfm.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.podata.AccountListAdapterData
import io.github.kedaitayar.mfm.data.repository.AccountRepository
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.podata.BudgetListAdapterData
import io.github.kedaitayar.mfm.data.podata.BudgetedAndGoal
import java.time.OffsetDateTime
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {
    val accountListAdapterData: LiveData<List<AccountListAdapterData>> =
        accountRepository.getAccountListData()
    val totalBudgetedAmount: LiveData<Double> = accountRepository.getTotalBudgetedAmount()
    val totalIncome: LiveData<Double> = accountRepository.getTotalIncome()
    val thisMonthSpending: LiveData<Double> = getThisMonthSpendingData()
    val nextMonthBudgeted: LiveData<Double> = getMonthBudgeted()
    val totalBudgetedAndGoal: LiveData<BudgetedAndGoal> = getUncompletedBudget()

    private fun getThisMonthSpendingData(): LiveData<Double> {
        val now = OffsetDateTime.now()
        val timeFrom =
            OffsetDateTime.of(now.year, now.monthValue, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0))
        val timeTo = timeFrom.plusMonths(1).minusNanos(1)
        return accountRepository.getMonthSpending(timeFrom, timeTo)
    }

    private fun getMonthBudgeted(): LiveData<Double> {
        val now = OffsetDateTime.now()
        return accountRepository.getMonthBudgeted(now.monthValue + 1, now.year)
    }

    private fun getUncompletedBudget(): LiveData<BudgetedAndGoal> {
        val now = OffsetDateTime.now()
        return accountRepository.getUncompletedBudget(now.monthValue, now.year)
    }

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