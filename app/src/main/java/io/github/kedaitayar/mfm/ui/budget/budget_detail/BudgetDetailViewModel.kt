package io.github.kedaitayar.mfm.ui.budget.budget_detail

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.repository.BudgetRepository
import javax.inject.Inject

@HiltViewModel
class BudgetDetailViewModel @Inject constructor(
    budgetRepository: BudgetRepository
) : ViewModel() {
    val budgets = budgetRepository.getAllBudgetFlow()
}