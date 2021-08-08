package io.github.kedaitayar.mfm.ui.budget.single_budgeting

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.entity.BudgetTransaction
import io.github.kedaitayar.mfm.data.podata.BudgetListAdapterData
import io.github.kedaitayar.mfm.data.repository.BasicRepository
import io.github.kedaitayar.mfm.data.repository.SelectedDateRepository
import io.github.kedaitayar.mfm.data.repository.TransactionRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

private const val TAG = "SingleBudgetingViewMode"

@HiltViewModel
class SingleBudgetingViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val basicRepository: BasicRepository,
    selectedDateRepository: SelectedDateRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val singleBudgetingEventChannel = Channel<SingleBudgetingEvent>()
    val singleBudgetingEvent = singleBudgetingEventChannel.receiveAsFlow()

    private val selectedDate: LiveData<LocalDateTime> = selectedDateRepository.selectedDate
    val budget = savedStateHandle.get<BudgetListAdapterData>("budget")
    private val monthlyBudgetFlow = selectedDate.asFlow().flatMapLatest {
        transactionRepository.getBudgetMonthlyListAdapterFlow(it.monthValue, it.year)
    }
    private val yearlyBudgetFlow = selectedDate.asFlow().flatMapLatest {
        transactionRepository.getBudgetYearlyListAdapterFlow(it.monthValue, it.year)
    }
    val allBudget =
        monthlyBudgetFlow.combine(yearlyBudgetFlow) { monthly: List<BudgetListAdapterData>, yearly: List<BudgetListAdapterData> ->
            monthly + yearly
        }.asLiveData()
    val inputAmount = MutableStateFlow("")
    val inputChip = MutableStateFlow(SingleBudgetingType.ADD)
    var inputBudget: BudgetListAdapterData? = null

    val budgetLabel = inputChip.mapLatest {
        when (it) {
            SingleBudgetingType.ADD -> ""
            SingleBudgetingType.MINUS -> ""
            SingleBudgetingType.GIVE -> GIVE_LABEL
            SingleBudgetingType.TAKE -> TAKE_LABEL
        }
    }

    fun onSubmit() {
        val selectedDateValue = selectedDate.value
        val amount = inputAmount.value.toDoubleOrNull()
        if (budget == null || selectedDateValue == null) return

        when (inputChip.value) {
            SingleBudgetingType.ADD -> {
                if (amount == null) {
                    viewModelScope.launch {
                        singleBudgetingEventChannel.send(SingleBudgetingEvent.InvalidInput(amountErrorMessage = "Required"))
                    }
                    return
                }
                val calculatedBudgetAmount = (budget.budgetAllocation + amount)
                val budgetTransaction = BudgetTransaction(
                    budgetTransactionMonth = selectedDateValue.monthValue,
                    budgetTransactionYear = selectedDateValue.year,
                    budgetTransactionAmount = calculatedBudgetAmount,
                    budgetTransactionBudgetId = budget.budgetId
                )
                viewModelScope.launch {
//                    val result = basicRepository.update(budgetTransaction)
                    val result = basicRepository.upsert(budgetTransaction)
                    singleBudgetingEventChannel.send(SingleBudgetingEvent.NavigateBackWithResult(result))
                }
            }
            SingleBudgetingType.MINUS -> {
                if (amount == null) {
                    viewModelScope.launch {
                        singleBudgetingEventChannel.send(SingleBudgetingEvent.InvalidInput(amountErrorMessage = "Required"))
                    }
                    return
                }
                val calculatedBudgetAmount = (budget.budgetAllocation - amount)
                val budgetTransaction = BudgetTransaction(
                    budgetTransactionMonth = selectedDateValue.monthValue,
                    budgetTransactionYear = selectedDateValue.year,
                    budgetTransactionAmount = calculatedBudgetAmount,
                    budgetTransactionBudgetId = budget.budgetId
                )
                viewModelScope.launch {
                    val result = basicRepository.upsert(budgetTransaction)
                    singleBudgetingEventChannel.send(SingleBudgetingEvent.NavigateBackWithResult(result))
                }
            }
            SingleBudgetingType.GIVE -> {
                val toBudget = inputBudget
                if (amount == null || toBudget == null) {
                    viewModelScope.launch {
                        singleBudgetingEventChannel.send(
                            SingleBudgetingEvent.InvalidInput(
                                amountErrorMessage = if (amount == null) "Required" else null,
                                budgetErrorMessage = if (toBudget == null) "Required" else null
                            )
                        )
                    }
                    return
                }
                val calculatedBudgetAmount = (budget.budgetAllocation - amount)
                val budgetTransaction = BudgetTransaction(
                    budgetTransactionMonth = selectedDateValue.monthValue,
                    budgetTransactionYear = selectedDateValue.year,
                    budgetTransactionAmount = calculatedBudgetAmount,
                    budgetTransactionBudgetId = budget.budgetId
                )
                val calculatedBudgetAmount2 = (toBudget.budgetAllocation + amount)
                val budgetTransaction2 = BudgetTransaction(
                    budgetTransactionMonth = selectedDateValue.monthValue,
                    budgetTransactionYear = selectedDateValue.year,
                    budgetTransactionAmount = calculatedBudgetAmount2,
                    budgetTransactionBudgetId = toBudget.budgetId
                )
                viewModelScope.launch {
                    val result = basicRepository.upsert(budgetTransaction)
                    val result2 = basicRepository.upsert(budgetTransaction2)
                    if (result == result2) {
                        singleBudgetingEventChannel.send(SingleBudgetingEvent.NavigateBackWithResult(result))
                    } else {
                        Log.w(
                            TAG,
                            "Please check this unexpected behaviour. ${this@SingleBudgetingViewModel.javaClass.`package`?.name}"
                        )
                    }
                }
            }
            SingleBudgetingType.TAKE -> {
                val fromBudget = inputBudget
                if (amount == null || fromBudget == null) {
                    viewModelScope.launch {
                        singleBudgetingEventChannel.send(
                            SingleBudgetingEvent.InvalidInput(
                                amountErrorMessage = if (amount == null) "Required" else null,
                                budgetErrorMessage = if (fromBudget == null) "Required" else null
                            )
                        )
                    }
                    return
                }
                val calculatedBudgetAmount = (budget.budgetAllocation + amount)
                val budgetTransaction = BudgetTransaction(
                    budgetTransactionMonth = selectedDateValue.monthValue,
                    budgetTransactionYear = selectedDateValue.year,
                    budgetTransactionAmount = calculatedBudgetAmount,
                    budgetTransactionBudgetId = budget.budgetId
                )
                val calculatedBudgetAmount2 = (fromBudget.budgetAllocation - amount)
                val budgetTransaction2 = BudgetTransaction(
                    budgetTransactionMonth = selectedDateValue.monthValue,
                    budgetTransactionYear = selectedDateValue.year,
                    budgetTransactionAmount = calculatedBudgetAmount2,
                    budgetTransactionBudgetId = fromBudget.budgetId
                )
                viewModelScope.launch {
                    val result = basicRepository.upsert(budgetTransaction)
                    val result2 = basicRepository.upsert(budgetTransaction2)
                    if (result == result2) {
                        singleBudgetingEventChannel.send(SingleBudgetingEvent.NavigateBackWithResult(result))
                    } else {
                        Log.w(TAG, "Please check this unexpected behaviour. ${this@SingleBudgetingViewModel.javaClass.`package`?.name}")
                    }
                }
            }
        }
    }


    enum class SingleBudgetingType {
        ADD, MINUS, GIVE, TAKE
    }

    companion object {
        const val GIVE_LABEL = "Give to"
        const val TAKE_LABEL = "Take from"
    }

    sealed class SingleBudgetingEvent {
        data class NavigateBackWithResult(val result: Boolean) : SingleBudgetingEvent()
        data class InvalidInput(val amountErrorMessage: String? = null, val budgetErrorMessage: String? = null) :
            SingleBudgetingEvent()
    }
}