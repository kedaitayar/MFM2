package io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.entity.QuickTransaction
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
    val quickTransaction = savedStateHandle.get<QuickTransaction>("quickTransaction")
    var inputAccountFrom: AccountListAdapterData? = null
    var inputBudget: BudgetListAdapterData? = null
    var inputAccountTo: AccountListAdapterData? = null
    var inputAmount: Double? = null
    val inputDate = MutableStateFlow(OffsetDateTime.now())
    var inputNote: String = ""

    val errorAccountFrom = MutableStateFlow<String?>(null)
     val errorBudget = MutableStateFlow<String?>(null)
     val errorAccountTo = MutableStateFlow<String?>(null)
     val errorAmount = MutableStateFlow<String?>(null)

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
        if (transaction != null) {
            return@map value.find { accountListAdapterData -> accountListAdapterData.accountId == transaction.transactionAccountId }
        } else if (quickTransaction != null) {
            return@map value.find { accountListAdapterData -> accountListAdapterData.accountId == quickTransaction.transactionAccountId }
        }
        return@map null
    }.asLiveData()
    val budget = monthlyBudgetFlow.combine(yearlyBudgetFlow) { monthly, yearly ->
        val budget = monthly + yearly
        if (transaction != null) {
            return@combine budget.find { budgetListAdapterData -> budgetListAdapterData.budgetId == transaction.transactionBudgetId }
        } else if (quickTransaction != null) {
            return@combine budget.find { budgetListAdapterData -> budgetListAdapterData.budgetId == quickTransaction.transactionBudgetId }
        }
        return@combine null
    }.asLiveData()
    val accountTo = allAccountFlow.map { value: List<AccountListAdapterData> ->
        if (transaction != null) {
            return@map value.find { accountListAdapterData -> accountListAdapterData.accountId == transaction.transactionAccountTransferTo }
        } else if (quickTransaction != null) {
            return@map value.find { accountListAdapterData -> accountListAdapterData.accountId == quickTransaction.transactionAccountTransferTo }
        }
        return@map null
    }.asLiveData()

    fun onButtonSaveClick(transactionType: TransactionType) {
        viewModelScope.launch {
            when (transactionType) {
                TransactionType.EXPENSE -> {
                    if (inputAccountFrom == null) {
                        errorAccountFrom.value = "Account cannot be empty"
                    }
                    if (inputBudget == null) {
                        errorBudget.value = "Budget cannot be empty"
                    }
                    if (inputAmount == null) {
                        errorAmount.value = "Amount cannot be empty"
                    }
                    if (inputAccountFrom !== null || inputBudget !== null || inputAmount !== null) {
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
                TransactionType.INCOME -> {
                    when {
                        inputAccountFrom == null -> {
                            errorAccountFrom.value = "Account cannot be empty"
                        }
                        inputAmount == null -> {
                            errorAmount.value = "Amount cannot be empty"
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
                            errorAccountFrom.value = "Account cannot be empty"
                        }
                        inputAccountTo == null -> {
                            errorAccountTo.value = "Account cannot be empty"
                        }
                        inputAmount == null -> {
                            errorAmount.value = "Amount cannot be empty"
                        }
                        inputAccountFrom == inputAccountTo -> {
                            errorAccountTo.value =
                                "'Account From' and 'Account To' cannot be the same"
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
            }
        }
    }

    fun onButtonAddClick(transactionType: TransactionType) {
        viewModelScope.launch {
            when (transactionType) {
                TransactionType.EXPENSE -> {
                    if (inputAccountFrom == null) {
                        errorAccountFrom.value = "Account cannot be empty"
                    }
                    if (inputBudget == null) {
                        errorBudget.value = "Budget cannot be empty"
                    }
                    if (inputAmount == null) {
                        errorAmount.value = "Amount cannot be empty"
                    }
                    if (inputAccountFrom !== null || inputBudget !== null || inputAmount !== null) {
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
                TransactionType.INCOME -> {
                    when {
                        inputAccountFrom == null -> {
                            errorAccountFrom.value = "Account cannot be empty"
                        }
                        inputAmount == null -> {
                            errorAmount.value = "Amount cannot be empty"
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
                            errorAccountFrom.value = "Account cannot be empty"
                        }
                        inputAccountTo == null -> {
                            errorAccountTo.value = "Account cannot be empty"
                        }
                        inputAmount == null -> {
                            errorAmount.value = "Amount cannot be empty"
                        }
                        inputAccountFrom == inputAccountTo -> {
                            errorAccountTo.value =
                                "'Account From' and 'Account To' cannot be the same"
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
            }
        }
    }

    fun onDeleteClick() {
        viewModelScope.launch {
            if (transaction != null) {
                val result =
                    transactionRepository.delete(Transaction(transactionId = transaction.transactionId))
                addEditTransactionEventChannel.send(
                    AddEditTransactionEvent.NavigateBackWithDeleteResult(
                        result
                    )
                )
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