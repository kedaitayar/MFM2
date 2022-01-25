package io.github.kedaitayar.mfm.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.entity.BudgetTransaction
import io.github.kedaitayar.mfm.data.repository.BudgetRepository
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class MainBudgetViewModel
@Inject constructor(
    private val budgetRepository: BudgetRepository,
) : ViewModel() {
    private val now: OffsetDateTime = OffsetDateTime.now()

    fun onReclaimUnusedBudgetClick() {
        viewModelScope.launch {
            val reclaimBudgetTransactionData = getBudgetTransactionJoinTransactionSuspend().filter { item ->
                when {
                    item.budgetTransactionMonth < now.monthValue && item.budgetTransactionYear <= now.year && item.budgetTransactionAmount > item.transactionAmount && item.budgetType == 1 -> {
                        true
                    }
                    item.budgetType == 2 && item.budgetTransactionYear < now.year && item.budgetTransactionAmount > item.transactionAmount -> {
                        true
                    }
                    item.budgetTransactionYear < now.year && item.budgetTransactionAmount > item.transactionAmount && item.budgetType == 1 -> {
                        true
                    }
                    else -> false
                }
            }
            for (item in reclaimBudgetTransactionData) {
                val budgetTransaction = BudgetTransaction(
                    budgetTransactionMonth = item.budgetTransactionMonth,
                    budgetTransactionYear = item.budgetTransactionYear,
                    budgetTransactionAmount = item.transactionAmount,
                    budgetTransactionBudgetId = item.budgetId
                )
                budgetRepository.update(budgetTransaction)
            }
        }
    }

    private suspend fun getBudgetTransactionJoinTransactionSuspend() =
        budgetRepository.getBudgetTransactionJoinTransactionSuspend()
}