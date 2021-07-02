package io.github.kedaitayar.mfm.ui.budget.budget_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.podata.BudgetListAdapterData
import io.github.kedaitayar.mfm.data.repository.BudgetRepository
import io.github.kedaitayar.mfm.data.repository.SelectedDateRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class BudgetListViewModel
@Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val selectedDateRepository: SelectedDateRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val budgetListEventChannel = Channel<BudgetListEvent>()
    val budgetListEvent = budgetListEventChannel.receiveAsFlow()

    val budgetType = savedStateHandle.get<Int>(BudgetListFragment.ARG_BUDGET_TYPE)
    private val selectedDate: LiveData<LocalDateTime> = selectedDateRepository.selectedDate
    val budgetList = getBudgetListData()

    private fun getBudgetListData(): LiveData<List<BudgetListAdapterData>> {
        return when (budgetType) {
            1 -> {
                Transformations.switchMap(selectedDate) {
                    budgetRepository.getBudgetMonthlyListAdapter(it.monthValue, it.year)
                }
            }
            2 -> {
                Transformations.switchMap(selectedDate) {
                    budgetRepository.getBudgetYearlyListAdapter(it.monthValue, it.year)
                }
            }
            else -> {
                Transformations.switchMap(selectedDate) {
                    budgetRepository.getBudgetMonthlyListAdapter(it.monthValue, it.year)
                }
            }
        }
    }

    sealed class BudgetListEvent {
        data class ShowSnackbar(val msg: String, val length: Int) : BudgetListEvent()
        data class NavigateBackWithAddResult(val result: Long) : BudgetListEvent()
        data class NavigateBackWithEditResult(val result: Int) : BudgetListEvent()
        data class NavigateBackWithDeleteResult(val result: Int) : BudgetListEvent()
    }
}