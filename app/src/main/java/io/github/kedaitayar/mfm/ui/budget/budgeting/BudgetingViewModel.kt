package io.github.kedaitayar.mfm.ui.budget.budgeting

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.entity.BudgetTransaction
import io.github.kedaitayar.mfm.data.repository.BudgetRepository
import io.github.kedaitayar.mfm.data.repository.SelectedDateRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class BudgetingViewModel
@Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val selectedDateRepository: SelectedDateRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val budgetingEventChannel = Channel<BudgetingEvent>()
    val budgetingEvent = budgetingEventChannel.receiveAsFlow()

    val selectedDate: LiveData<LocalDateTime> = selectedDateRepository.selectedDate
    val monthlyBudgetingListData = selectedDate.switchMap {
        budgetRepository.getMonthlyBudgetingListAdapterFlow(it.monthValue, it.year).asLiveData()
    }
    val yearlyBudgetingListData = selectedDate.switchMap {
        budgetRepository.getYearlyBudgetingListAdapterFlow(it.monthValue, it.year).asLiveData()
    }
    private val totalIncome = budgetRepository.getTotalIncome()
    private val totalBudgeted = budgetRepository.getTotalBudgetedAmount()
    val totalNotBudgeted = combine(totalIncome, totalBudgeted) { income: Double, budgeted: Double ->
        income - budgeted
    }.asLiveData()

    var totalMonthlyBudgetedThisMonth: Double = 0.0
    var totalYearlyBudgetedThisMonth: Double = 0.0
    var budgetingAmountListMonthly = mutableMapOf<Long, String>()
    var budgetingAmountListYearly = mutableMapOf<Long, String>()

    fun onSaveClick() {
        viewModelScope.launch {
            val monthlyBudgetingList = monthlyBudgetingListData.value
            val date = selectedDate.value!!
            if (monthlyBudgetingList != null) {
                for (budgeting in monthlyBudgetingList) {
                    val budgetTransaction = BudgetTransaction(
                        budgetTransactionAmount = ((budgetingAmountListMonthly[budgeting.budgetId])?.toDouble()
                            ?: 0.0),
                        budgetTransactionBudgetId = budgeting.budgetId,
                        budgetTransactionMonth = date.monthValue,
                        budgetTransactionYear = date.year
                    )
                    budgetRepository.insert(budgetTransaction)
                }
            }

            val yearlyBudgetingList = yearlyBudgetingListData.value
            if (yearlyBudgetingList != null) {
                for (budgeting in yearlyBudgetingList) {
                    val budgetTransaction = BudgetTransaction(
                        budgetTransactionAmount = ((budgetingAmountListYearly[budgeting.budgetId])?.toDouble()
                            ?: 0.0),
                        budgetTransactionBudgetId = budgeting.budgetId,
                        budgetTransactionMonth = date.monthValue,
                        budgetTransactionYear = date.year
                    )
                    budgetRepository.insert(budgetTransaction)
                }
            }
            budgetingEventChannel.send(BudgetingEvent.NavigateBack)
        }
    }

    sealed class BudgetingEvent {
        data class ShowSnackbar(val msg: String, val length: Int) : BudgetingEvent()
        object NavigateBack : BudgetingEvent()
    }
}