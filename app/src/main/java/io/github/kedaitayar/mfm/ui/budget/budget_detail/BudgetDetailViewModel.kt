package io.github.kedaitayar.mfm.ui.budget.budget_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.repository.BudgetRepository
import io.github.kedaitayar.mfm.ui.main.MainFragmentDirections
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class BudgetDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    budgetRepository: BudgetRepository
) : ViewModel() {
    private val budgetId = BudgetDetailFragmentArgs.fromSavedStateHandle(savedStateHandle).budgetId
    private val monthlySpendingByBudget =
        budgetRepository.getMonthlySpendingGraphDataByBudget(budgetId)
    val monthlyBudgetSpendingGraph = monthlySpendingByBudget.map { raw ->
        val barEntries = mutableListOf<BarEntry>()
        raw.forEachIndexed { index, monthlySpendingData ->
            barEntries.add(
                BarEntry(
                    index.toFloat(),
                    monthlySpendingData.monthSpending.toFloat()
                )
            )
        }
        barEntries
    }
    val transactionListByBudget =
        budgetRepository.getTransactionListByBudgetData(budgetId).cachedIn(viewModelScope)
}