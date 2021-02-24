package io.github.kedaitayar.mfm.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.podata.SelectedDate
import io.github.kedaitayar.mfm.data.entity.BudgetType
import io.github.kedaitayar.mfm.data.repository.BudgetRepository
import io.github.kedaitayar.mfm.data.repository.SelectedDateRepository
import io.github.kedaitayar.mfm.data.entity.Budget
import io.github.kedaitayar.mfm.data.entity.BudgetTransaction
import io.github.kedaitayar.mfm.data.podata.BudgetListAdapterData
import java.time.LocalDateTime
import javax.inject.Inject

private const val TAG = "BudgetViewModel"

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val selectedDateRepository: SelectedDateRepository
) : ViewModel() {
    val allBudgetType: LiveData<List<BudgetType>> = budgetRepository.getAllBudgetType()
    val allBudget: LiveData<List<Budget>> = budgetRepository.getAllBudget()
    val selectedDate: LiveData<LocalDateTime> = selectedDateRepository.selectedDate
    val monthlyBudgetListData: LiveData<List<BudgetListAdapterData>> =
        Transformations.switchMap(selectedDate) {
            Log.i(TAG, "monthlyBudgetListData: ${selectedDate.value}")
            budgetRepository.getBudgetMonthlyListAdapterDO(it.monthValue, it.year)
        }
    val budgetingListData: LiveData<List<BudgetListAdapterData>> =
        Transformations.switchMap(selectedDate) {
            Log.i(TAG, "budgetingListData: ${selectedDate.value}")
            budgetRepository.getBudgetingListAdapterDO(it.monthValue, it.year)
        }


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

    //budgetTransaction
    fun getAllBudgetTransaction(): LiveData<List<BudgetTransaction>> {
        return budgetRepository.getAllBudgetTransaction()
    }

    suspend fun insert(budgetTransaction: BudgetTransaction): Long {
        return budgetRepository.insert(budgetTransaction)
    }

    suspend fun update(budgetTransaction: BudgetTransaction): Int {
        return budgetRepository.update(budgetTransaction)
    }

    suspend fun delete(budgetTransaction: BudgetTransaction): Int {
        return budgetRepository.delete(budgetTransaction)
    }

    //selected date
    fun increaseMonth() {
        selectedDateRepository.increaseMonth()
    }

    fun decreaseMonth() {
        selectedDateRepository.decreaseMonth()
    }
}