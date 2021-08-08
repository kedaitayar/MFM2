package io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.entity.Transaction
import io.github.kedaitayar.mfm.data.podata.AccountListAdapterData
import io.github.kedaitayar.mfm.data.podata.BudgetListAdapterData
import io.github.kedaitayar.mfm.data.repository.TransactionRepository
import io.github.kedaitayar.mfm.util.exhaustive
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class AddEditTransactionViewModel
@Inject
constructor(
    private val transactionRepository: TransactionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var inputAccountFrom: AccountListAdapterData? = null
    var inputBudget: BudgetListAdapterData? = null
    var inputAccountTo: AccountListAdapterData? = null
    var inputAmount: Double? = null
    val inputDate = MutableStateFlow(OffsetDateTime.now())
    var inputNote: String = ""


    //    private val now = OffsetDateTime.now()
    private val addEditTransactionEventChannel = Channel<AddEditTransactionEvent>()
    val addEditTransactionEvent = addEditTransactionEventChannel.receiveAsFlow()

    val transaction = savedStateHandle.get<Transaction>("transaction")
    private val allAccountFlow = transactionRepository.getAccountListDataFlow()
    val allAccount = allAccountFlow.asLiveData()

    private val monthlyBudgetFlow = inputDate.flatMapLatest {
        transactionRepository.getBudgetMonthlyListAdapterFlow(it.monthValue, it.year)
    }
    private val yearlyBudgetFlow = inputDate.flatMapLatest {
        transactionRepository.getBudgetYearlyListAdapterFlow(it.monthValue, it.year)
    }
    val allBudget =
        monthlyBudgetFlow.combine(yearlyBudgetFlow) { monthly: List<BudgetListAdapterData>, yearly: List<BudgetListAdapterData> ->
            monthly + yearly
        }.asLiveData()

    val accountFrom = allAccountFlow.map { value: List<AccountListAdapterData> ->
        value.find { accountListAdapterData -> accountListAdapterData.accountId == transaction!!.transactionAccountId }
    }.asLiveData()
    val budget = monthlyBudgetFlow.combine(yearlyBudgetFlow) { monthly, yearly ->
        val budget = monthly + yearly
        budget.find { budgetListAdapterData -> budgetListAdapterData.budgetId == transaction!!.transactionBudgetId }
    }.asLiveData()
    val accountTo = allAccountFlow.map { value: List<AccountListAdapterData> ->
        value.find { accountListAdapterData -> accountListAdapterData.accountId == transaction!!.transactionAccountTransferTo }
    }.asLiveData()

    fun onButtonSaveClick(transactionType: TransactionType) {
        viewModelScope.launch {
            when (transactionType) {
                TransactionType.EXPENSE -> {
                    when {
                        inputAccountFrom == null -> {
                            addEditTransactionEventChannel.send(
                                AddEditTransactionEvent.ShowSnackbar(
                                    "Account cannot be empty",
                                    Snackbar.LENGTH_SHORT
                                )
                            )
                        }
                        inputBudget == null -> {
                            addEditTransactionEventChannel.send(
                                AddEditTransactionEvent.ShowSnackbar(
                                    "Budget cannot be empty",
                                    Snackbar.LENGTH_SHORT
                                )
                            )
                        }
                        inputAmount == null -> {
                            addEditTransactionEventChannel.send(
                                AddEditTransactionEvent.ShowSnackbar(
                                    "Amount cannot be empty",
                                    Snackbar.LENGTH_SHORT
                                )
                            )
                        }
                        else -> {
                            val transaction =
                                transaction?.copy(
                                    transactionAccountId = inputAccountFrom!!.accountId,
                                    transactionBudgetId = inputBudget!!.budgetId,
                                    transactionAmount = inputAmount!!,
                                    transactionTime = inputDate.value,
                                    transactionNote = inputNote
                                )
                            transaction?.let {
                                val result = update(transaction)
                                addEditTransactionEventChannel.send(
                                    AddEditTransactionEvent.NavigateBackWithEditResult(
                                        result
                                    )
                                )

                            }
                        }
                    }
                }
                TransactionType.INCOME -> {
                    when {
                        inputAccountFrom == null -> {
                            addEditTransactionEventChannel.send(
                                AddEditTransactionEvent.ShowSnackbar(
                                    "Account cannot be empty",
                                    Snackbar.LENGTH_SHORT
                                )
                            )
                        }
                        inputAmount == null -> {
                            addEditTransactionEventChannel.send(
                                AddEditTransactionEvent.ShowSnackbar(
                                    "Amount cannot be empty",
                                    Snackbar.LENGTH_SHORT
                                )
                            )
                        }
                        else -> {
                            val transaction =
                                transaction?.copy(
                                    transactionAccountId = inputAccountFrom!!.accountId,
                                    transactionAmount = inputAmount!!,
                                    transactionTime = inputDate.value,
                                    transactionNote = inputNote
                                )
                            transaction?.let {
                                val result = update(transaction)
                                addEditTransactionEventChannel.send(
                                    AddEditTransactionEvent.NavigateBackWithEditResult(
                                        result
                                    )
                                )
                            }
                        }
                    }
                }
                TransactionType.TRANSFER -> {
                    when {
                        inputAccountFrom == null -> {
                            addEditTransactionEventChannel.send(
                                AddEditTransactionEvent.ShowSnackbar(
                                    "Account cannot be empty",
                                    Snackbar.LENGTH_SHORT
                                )
                            )
                        }
                        inputAccountTo == null -> {
                            addEditTransactionEventChannel.send(
                                AddEditTransactionEvent.ShowSnackbar(
                                    "Account cannot be empty",
                                    Snackbar.LENGTH_SHORT
                                )
                            )
                        }
                        inputAmount == null -> {
                            addEditTransactionEventChannel.send(
                                AddEditTransactionEvent.ShowSnackbar(
                                    "Amount cannot be empty",
                                    Snackbar.LENGTH_SHORT
                                )
                            )
                        }
                        inputAccountFrom == inputAccountTo -> {
                            addEditTransactionEventChannel.send(
                                AddEditTransactionEvent.ShowSnackbar(
                                    "'Account From' and 'Account To' cannot be empty",
                                    Snackbar.LENGTH_SHORT
                                )
                            )
                        }
                        else -> {
                            val transaction =
                                transaction?.copy(
                                    transactionAccountId = inputAccountFrom!!.accountId,
                                    transactionAccountTransferTo = inputAccountTo!!.accountId,
                                    transactionAmount = inputAmount!!,
                                    transactionTime = inputDate.value,
                                    transactionNote = inputNote
                                )
                            transaction?.let {
                                val result = update(transaction)
                                addEditTransactionEventChannel.send(
                                    AddEditTransactionEvent.NavigateBackWithEditResult(
                                        result
                                    )
                                )
                            }
                        }
                    }
                }
            }.exhaustive
        }
    }

    fun onButtonAddClick(transactionType: TransactionType) {
        viewModelScope.launch {
            when (transactionType) {
                TransactionType.EXPENSE -> {
                    when {
                        inputAccountFrom == null -> {
                            addEditTransactionEventChannel.send(
                                AddEditTransactionEvent.ShowSnackbar(
                                    "Account cannot be empty",
                                    Snackbar.LENGTH_SHORT
                                )
                            )
                        }
                        inputBudget == null -> {
                            addEditTransactionEventChannel.send(
                                AddEditTransactionEvent.ShowSnackbar(
                                    "Budget cannot be empty",
                                    Snackbar.LENGTH_SHORT
                                )
                            )
                        }
                        inputAmount == null -> {
                            addEditTransactionEventChannel.send(
                                AddEditTransactionEvent.ShowSnackbar(
                                    "Amount cannot be empty",
                                    Snackbar.LENGTH_SHORT
                                )
                            )
                        }
                        else -> {
                            val transaction = Transaction(
                                transactionAccountId = inputAccountFrom!!.accountId,
                                transactionBudgetId = inputBudget!!.budgetId,
                                transactionAmount = inputAmount ?: 0.0,
                                transactionType = 1,
                                transactionTime = inputDate.value,
                                transactionNote = inputNote
                            )
                            val result = insert(transaction)
                            addEditTransactionEventChannel.send(
                                AddEditTransactionEvent.NavigateBackWithAddResult(
                                    result
                                )
                            )
                        }
                    }
                }
                TransactionType.INCOME -> {
                    when {
                        inputAccountFrom == null -> {
                            addEditTransactionEventChannel.send(
                                AddEditTransactionEvent.ShowSnackbar(
                                    "Account cannot be empty",
                                    Snackbar.LENGTH_SHORT
                                )
                            )
                        }
                        inputAmount == null -> {
                            addEditTransactionEventChannel.send(
                                AddEditTransactionEvent.ShowSnackbar(
                                    "Amount cannot be empty",
                                    Snackbar.LENGTH_SHORT
                                )
                            )
                        }
                        else -> {
                            val transaction = Transaction(
                                transactionAccountId = inputAccountFrom!!.accountId,
                                transactionAmount = inputAmount ?: 0.0,
                                transactionType = 2,
                                transactionTime = inputDate.value,
                                transactionNote = inputNote
                            )
                            val result = insert(transaction)
                            addEditTransactionEventChannel.send(
                                AddEditTransactionEvent.NavigateBackWithAddResult(
                                    result
                                )
                            )
                        }
                    }
                }
                TransactionType.TRANSFER -> {
                    when {
                        inputAccountFrom == null -> {
                            addEditTransactionEventChannel.send(
                                AddEditTransactionEvent.ShowSnackbar(
                                    "Account cannot be empty",
                                    Snackbar.LENGTH_SHORT
                                )
                            )
                        }
                        inputAccountTo == null -> {
                            addEditTransactionEventChannel.send(
                                AddEditTransactionEvent.ShowSnackbar(
                                    "Account cannot be empty",
                                    Snackbar.LENGTH_SHORT
                                )
                            )
                        }
                        inputAmount == null -> {
                            addEditTransactionEventChannel.send(
                                AddEditTransactionEvent.ShowSnackbar(
                                    "Amount cannot be empty",
                                    Snackbar.LENGTH_SHORT
                                )
                            )
                        }
                        inputAccountFrom == inputAccountTo -> {
                            addEditTransactionEventChannel.send(
                                AddEditTransactionEvent.ShowSnackbar(
                                    "'Account From' and 'Account To' cannot be empty",
                                    Snackbar.LENGTH_SHORT
                                )
                            )
                        }
                        else -> {
                            val transaction = Transaction(
                                transactionAccountId = inputAccountFrom!!.accountId,
                                transactionAmount = inputAmount ?: 0.0,
                                transactionAccountTransferTo = inputAccountTo!!.accountId,
                                transactionType = 3,
                                transactionTime = inputDate.value,
                                transactionNote = inputNote
                            )
                            val result = insert(transaction)
                            addEditTransactionEventChannel.send(
                                AddEditTransactionEvent.NavigateBackWithAddResult(
                                    result
                                )
                            )
                        }
                    }
                }
            }.exhaustive
        }
    }

    fun onDeleteClick() {
        viewModelScope.launch {
            if (transaction != null) {
                val result = transactionRepository.delete(Transaction(transactionId = transaction.transactionId))
                addEditTransactionEventChannel.send(AddEditTransactionEvent.NavigateBackWithDeleteResult(result))
            }
        }
    }

    suspend fun insert(transaction: Transaction): Long {
        return transactionRepository.insert(transaction)
    }

    suspend fun delete(transaction: Transaction): Int {
        return transactionRepository.delete(transaction)
    }

    suspend fun update(transaction: Transaction): Int {
        return transactionRepository.update(transaction)
    }

    enum class TransactionType { EXPENSE, INCOME, TRANSFER }

    sealed class AddEditTransactionEvent {
        data class ShowSnackbar(val msg: String, val length: Int) : AddEditTransactionEvent()
        data class NavigateBackWithAddResult(val result: Long) : AddEditTransactionEvent()
        data class NavigateBackWithEditResult(val result: Int) : AddEditTransactionEvent()
        data class NavigateBackWithDeleteResult(val result: Int) : AddEditTransactionEvent()
    }
}