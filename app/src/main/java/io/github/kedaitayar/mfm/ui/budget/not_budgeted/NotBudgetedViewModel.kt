package io.github.kedaitayar.mfm.ui.budget.not_budgeted

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class NotBudgetedViewModel @Inject constructor(budgetRepository: BudgetRepository) : ViewModel() {
    private val totalIncome: Flow<Double?> = budgetRepository.getTotalIncome()
    private val totalBudgetedAmount: Flow<Double?> = budgetRepository.getTotalBudgetedAmount()
    val notBudgetedAmount =
        combine(totalIncome, totalBudgetedAmount) { totalIncome, totalBudgetedAmount ->
            (totalIncome ?: 0.0) - (totalBudgetedAmount ?: 0.0)
        }
}