package io.github.kedaitayar.mfm.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.podata.SelectedDate
import io.github.kedaitayar.mfm2.data.entity.BudgetType
import io.github.kedaitayar.mfm.data.repository.BudgetRepository
import io.github.kedaitayar.mfm.data.repository.SelectedDateRepository
import io.github.kedaitayar.mfm2.data.entity.Budget
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val selectedDateRepository: SelectedDateRepository
) : ViewModel() {
    val allBudgetType: LiveData<List<BudgetType>> = budgetRepository.getAllBudgetTypeLV()
    val allBudget: LiveData<List<Budget>> = budgetRepository.getAllBudget()
    val selectedDate: LiveData<SelectedDate> = selectedDateRepository.selectedDate

    suspend fun insert(budget: Budget): Long {
        return budgetRepository.insert(budget)
    }

    suspend fun update(budget: Budget): Int {
        return budgetRepository.update(budget)
    }

    suspend fun delete(budget: Budget): Int {
        return budgetRepository.delete(budget)
    }

    //budgetType
    suspend fun insert(budgetType: BudgetType): Long {
        return budgetRepository.insert(budgetType)
    }

    suspend fun deleteAllBudgetType() {
        budgetRepository.deleteAllBudgetType()
    }
}