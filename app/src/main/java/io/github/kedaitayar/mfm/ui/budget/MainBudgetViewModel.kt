package io.github.kedaitayar.mfm.ui.budget

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.entity.BudgetTransaction
import io.github.kedaitayar.mfm.data.podata.BudgetTransactionJoinTransaction
import io.github.kedaitayar.mfm.data.repository.BudgetRepository
import io.github.kedaitayar.mfm.data.repository.SelectedDateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class MainBudgetViewModel
@Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val selectedDateRepository: SelectedDateRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val selectedDate: LiveData<LocalDateTime> = selectedDateRepository.selectedDate
    private val now: OffsetDateTime = OffsetDateTime.now()
    private val allBudgetTransactionJoinTransactionFlow: Flow<List<BudgetTransactionJoinTransaction>> =
        budgetRepository.getBudgetTransactionJoinTransactionFlow()

    fun onReclaimUnusedBudgetClick() {
        viewModelScope.launch {
            val reclaimBudgetTransactionData = getBudgetTransactionJoinTransactionSuspend().filter { item ->
                when {
                    item.budgetTransactionMonth < now.monthValue && item.budgetTransactionYear <= now.year && item.budgetTransactionAmount > item.transactionAmount && item.budgetType == 1 -> {
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

    //selected date
    fun increaseMonth() {
        selectedDateRepository.increaseMonth()
    }

    fun decreaseMonth() {
        selectedDateRepository.decreaseMonth()
    }

    private suspend fun getBudgetTransactionJoinTransactionSuspend() =
        budgetRepository.getBudgetTransactionJoinTransactionSuspend()
}