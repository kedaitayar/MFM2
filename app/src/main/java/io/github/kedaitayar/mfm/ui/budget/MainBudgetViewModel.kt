package io.github.kedaitayar.mfm.ui.budget

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.podata.BudgetTransactionJoinTransaction
import io.github.kedaitayar.mfm.data.repository.BudgetRepository
import io.github.kedaitayar.mfm.data.repository.SelectedDateRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class MainBudgetViewModel
@Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val selectedDateRepository: SelectedDateRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel(){
    private val allBudgetTransactionJoinTransactionFlow: Flow<List<BudgetTransactionJoinTransaction>> = budgetRepository.getBudgetTransactionJoinTransactionFlow()
    val allBudgetTransactionJoinTransaction: LiveData<List<BudgetTransactionJoinTransaction>> = allBudgetTransactionJoinTransactionFlow.asLiveData()
    val selectedDate: LiveData<LocalDateTime> = selectedDateRepository.selectedDate

    fun onReclaimUnusedBudgetClick() {
        TODO("do this")
    }

    //selected date
    fun increaseMonth() {
        selectedDateRepository.increaseMonth()
    }

    fun decreaseMonth() {
        selectedDateRepository.decreaseMonth()
    }
}