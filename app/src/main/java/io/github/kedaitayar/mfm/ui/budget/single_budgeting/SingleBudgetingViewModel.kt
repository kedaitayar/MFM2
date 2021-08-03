package io.github.kedaitayar.mfm.ui.budget.single_budgeting

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.entity.BudgetTransaction
import io.github.kedaitayar.mfm.data.podata.BudgetListAdapterData
import io.github.kedaitayar.mfm.data.repository.SelectedDateRepository
import io.github.kedaitayar.mfm.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SingleBudgetingViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val selectedDateRepository: SelectedDateRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val selectedDate: LiveData<LocalDateTime> = selectedDateRepository.selectedDate
    private val now = OffsetDateTime.now()
    val budget = savedStateHandle.get<BudgetListAdapterData>("budget")
    private val monthlyBudgetFlow = transactionRepository.getBudgetMonthlyListAdapterFlow(now.monthValue, now.year)
    private val yearlyBudgetFlow = transactionRepository.getBudgetYearlyListAdapterFlow(now.monthValue, now.year)
    val allBudget =
        monthlyBudgetFlow.combine(yearlyBudgetFlow) { monthly: List<BudgetListAdapterData>, yearly: List<BudgetListAdapterData> ->
            monthly + yearly
        }.asLiveData()
    val inputAmount = MutableStateFlow("")
    var inputChip = SingleBudgetingType.ADD
    var inputBudget: BudgetListAdapterData? = null

    fun onSubmit() {
        viewModelScope.launch {
            when (inputChip) {
//                SingleBudgetingType.ADD -> {
//                    val budgetTransaction = BudgetTransaction(
//                        budgetTransactionMonth = budget?.
//                    )
//                }
//                SingleBudgetingType.MINUS -> TODO()
//                SingleBudgetingType.GIVE -> TODO()
//                SingleBudgetingType.TAKE -> TODO()
            }
        }
    }

    enum class SingleBudgetingType {
        ADD, MINUS, GIVE, TAKE
    }
}