package io.github.kedaitayar.mfm.ui.budget.add_edit_budget

import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.entity.Budget
import io.github.kedaitayar.mfm.data.entity.BudgetType
import io.github.kedaitayar.mfm.data.podata.BudgetListAdapterData
import io.github.kedaitayar.mfm.data.repository.BudgetRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditBudgetViewModel
@Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val addEditBudgetEventChanel = Channel<AddEditBudgetEvent>()
    val addEditBudgetEvent = addEditBudgetEventChanel.receiveAsFlow()
    val budgetListAdapterData = savedStateHandle.get<BudgetListAdapterData>("budgetListAdapterData")
    val allBudgetType: LiveData<List<BudgetType>> = budgetRepository.getAllBudgetTypeFlow().asLiveData()

    var inputBudgetName: String? = null
    var inputBudgetGoal: Double? = null
    var inputBudgetType: BudgetType? = null

    fun onSaveClick() {
        viewModelScope.launch {
            when {
                inputBudgetName.isNullOrBlank() -> {
                    addEditBudgetEventChanel.send(
                        AddEditBudgetEvent.ShowSnackbar(
                            "Budget name cannot be empty",
                            Snackbar.LENGTH_SHORT
                        )
                    )
                }
                inputBudgetType == null -> {
                    addEditBudgetEventChanel.send(
                        AddEditBudgetEvent.ShowSnackbar(
                            "Budget Type cannot be empty",
                            Snackbar.LENGTH_SHORT
                        )
                    )
                }
                else -> {
                    if (budgetListAdapterData != null) {
                        val budget = Budget(
                            budgetId = budgetListAdapterData.budgetId,
                            budgetGoal = inputBudgetGoal ?: 0.0,
                            budgetName = inputBudgetName!!,
                            budgetType = inputBudgetType!!.budgetTypeId!!.toInt()
                        )
                        val result = budgetRepository.update(budget)
                        addEditBudgetEventChanel.send(AddEditBudgetEvent.NavigateBackWithEditResult(result))
                    }
                }
            }
        }
    }

    fun onAddClick() {
        viewModelScope.launch {
            when {
                inputBudgetName.isNullOrBlank() -> {
                    addEditBudgetEventChanel.send(
                        AddEditBudgetEvent.ShowSnackbar(
                            "Budget name cannot be empty",
                            Snackbar.LENGTH_SHORT
                        )
                    )
                }
                inputBudgetType == null -> {
                    addEditBudgetEventChanel.send(
                        AddEditBudgetEvent.ShowSnackbar(
                            "Budget Type cannot be empty",
                            Snackbar.LENGTH_SHORT
                        )
                    )
                }
                else -> {
                    val budget = Budget(
//                        budgetId = 0,
                        budgetGoal = inputBudgetGoal ?: 0.0,
                        budgetName = inputBudgetName!!,
                        budgetType = inputBudgetType!!.budgetTypeId!!.toInt()
                    )
                    val result = budgetRepository.insert(budget)
                    addEditBudgetEventChanel.send(AddEditBudgetEvent.NavigateBackWithAddResult(result))
                }
            }
        }
    }

    fun onDeleteClick() {
        viewModelScope.launch {
            if (budgetListAdapterData != null) {
                val result = budgetRepository.delete(Budget(budgetId = budgetListAdapterData.budgetId))
                addEditBudgetEventChanel.send(AddEditBudgetEvent.NavigateBackWithDeleteResult(result))
            }
        }
    }

    sealed class AddEditBudgetEvent {
        data class ShowSnackbar(val msg: String, val length: Int) : AddEditBudgetEvent()
        data class NavigateBackWithAddResult(val result: Long) : AddEditBudgetEvent()
        data class NavigateBackWithEditResult(val result: Int) : AddEditBudgetEvent()
        data class NavigateBackWithDeleteResult(val result: Int) : AddEditBudgetEvent()
    }
}