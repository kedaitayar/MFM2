package io.github.kedaitayar.mfm.ui.budget.budgeting

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.entity.BudgetTransaction
import io.github.kedaitayar.mfm.data.repository.BasicRepository
import io.github.kedaitayar.mfm.data.repository.BudgetRepository
import io.github.kedaitayar.mfm.data.repository.SelectedDateRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BudgetingViewModel
@Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val basicRepository: BasicRepository,
    private val selectedDateRepository: SelectedDateRepository
) : ViewModel() {
    private val budgetingEventChannel = Channel<BudgetingEvent>()
    val budgetingEvent = budgetingEventChannel.receiveAsFlow()

    val selectedDate = selectedDateRepository.selectedDate

    val monthlyBudgetingListData = selectedDate.switchMap {
        budgetRepository.getMonthlyBudgetingListAdapterFlow(it.monthValue, it.year).asLiveData()
    }
    val yearlyBudgetingListData = selectedDate.switchMap {
        budgetRepository.getYearlyBudgetingListAdapterFlow(it.monthValue, it.year).asLiveData()
    }
    private val totalIncome = budgetRepository.getTotalIncome()
    private val totalBudgeted = budgetRepository.getTotalBudgetedAmount()
    private val totalNotBudgeted = combine(totalIncome, totalBudgeted) { income: Double?, budgeted: Double? ->
        (income ?: 0.0) - (budgeted ?: 0.0)
    }
    private val totalMonthlyBudgetedThisMonth = monthlyBudgetingListData.asFlow().flatMapLatest {
        flow {
            emit(it.map { item -> item.budgetAllocation }.sum())
        }
    }
    private val totalYearlyBudgetedThisMonth = yearlyBudgetingListData.asFlow().flatMapLatest {
        flow {
            emit(it.map { item -> item.budgetAllocation }.sum())
        }
    }
    val budgetingAmountListMonthly = MutableStateFlow(mapOf<Long, String>())
    val budgetingAmountListYearly = MutableStateFlow(mapOf<Long, String>())
    private val currentTotalMonthlyBudgetedThisMonth = budgetingAmountListMonthly.mapLatest { items ->
        items.map { item ->
            item.value.toDoubleOrNull() ?: 0.0
        }.sumOf { it }
    }
    private val currentTotalYearlyBudgetedThisMonth = budgetingAmountListYearly.mapLatest { items ->
        items.map { item ->
            item.value.toDoubleOrNull() ?: 0.0
        }.sumOf { it }
    }
    val currentTotalNotBudgeted = combine(
        totalNotBudgeted,
        totalMonthlyBudgetedThisMonth,
        totalYearlyBudgetedThisMonth,
        currentTotalMonthlyBudgetedThisMonth,
        currentTotalYearlyBudgetedThisMonth
    ) { totalNotBudgeted, totalMonthlyBudgetedThisMonth, totalYearlyBudgetedThisMonth, currentTotalMonthlyBudgetedThisMonth, currentTotalYearlyBudgetedThisMonth ->
        totalNotBudgeted - (currentTotalMonthlyBudgetedThisMonth - totalMonthlyBudgetedThisMonth) - (currentTotalYearlyBudgetedThisMonth - totalYearlyBudgetedThisMonth)
    }

    fun onSaveClick() {
        viewModelScope.launch {
            val monthlyBudgetingList = monthlyBudgetingListData.value
            val date = selectedDate.value!!
            if (monthlyBudgetingList != null) {
                for (budgeting in monthlyBudgetingList) {
                    val budgetTransaction = BudgetTransaction(
                        budgetTransactionAmount = ((budgetingAmountListMonthly.value[budgeting.budgetId])?.toDoubleOrNull()
                            ?: 0.0),
                        budgetTransactionBudgetId = budgeting.budgetId,
                        budgetTransactionMonth = date.monthValue,
                        budgetTransactionYear = date.year
                    )
//                    budgetRepository.insert(budgetTransaction)
                    basicRepository.upsert(budgetTransaction)
                }
            }
            val yearlyBudgetingList = yearlyBudgetingListData.value
            if (yearlyBudgetingList != null) {
                for (budgeting in yearlyBudgetingList) {
                    val budgetTransaction = BudgetTransaction(
                        budgetTransactionAmount = ((budgetingAmountListYearly.value[budgeting.budgetId])?.toDoubleOrNull()
                            ?: 0.0),
                        budgetTransactionBudgetId = budgeting.budgetId,
                        budgetTransactionMonth = date.monthValue,
                        budgetTransactionYear = date.year
                    )
//                    budgetRepository.insert(budgetTransaction)
                    basicRepository.upsert(budgetTransaction)
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