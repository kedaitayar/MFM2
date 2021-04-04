package io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction

import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.entity.Budget
import io.github.kedaitayar.mfm.data.entity.Transaction
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
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val addEditTransactionEventChannel = Channel<AddEditTransactionEvent>()
    val addEditTransactionEvent = addEditTransactionEventChannel.receiveAsFlow()

    val transaction = savedStateHandle.get<Transaction>("transaction")
    val allAccount = transactionRepository.getAllAccountFlow().asLiveData()
    val allBudget = transactionRepository.getAllBudgetFlow().asLiveData()
    val accountFrom = getAccountFromLivedata()
    val budget = getBudgetLivedata()
    val accountTo = getAccountToLivedata()

    var inputAccountFrom: Account? = null
    var inputBudget: Budget? = null
    var inputAccountTo: Account? = null
    var inputAmount: Double? = null

    private fun getAccountFromLivedata(): LiveData<Account?> {
        return when (transaction) {
            null -> {
                liveData<Account?> { emit(null) }
            }
            else -> {
                transactionRepository.getAccountByIdFlow(transaction.transactionAccountId).asLiveData()
            }
        }
    }

    private fun getBudgetLivedata(): LiveData<Budget?> {
        return when (transaction) {
            null -> {
                liveData<Budget?> { emit(null) }
            }
            else -> {
                when (transaction.transactionBudgetId) {
                    null -> {
                        liveData<Budget?> { emit(null) }
                    }
                    else -> {
                        transactionRepository.getBudgetByIdFlow(transaction.transactionBudgetId!!)
                            .asLiveData()
                    }
                }
            }
        }
    }

    private fun getAccountToLivedata(): LiveData<Account?> {
        return when (transaction) {
            null -> {
                liveData<Account?> { emit(null) }
            }
            else -> {
                when (transaction.transactionAccountTransferTo) {
                    null -> {
                        liveData<Account?> { emit(null) }
                    }
                    else -> {
                        transactionRepository.getAccountByIdFlow(transaction.transactionAccountTransferTo!!)
                            .asLiveData()
                    }
                }
            }
        }
    }


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
                                    transactionAccountId = inputAccountFrom!!.accountId!!,
                                    transactionBudgetId = inputBudget!!.budgetId,
                                    transactionAmount = inputAmount!!,

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
                                    transactionAccountId = inputAccountFrom!!.accountId!!,
                                    transactionAmount = inputAmount!!
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
                        else -> {
                            val transaction =
                                transaction?.copy(
                                    transactionAccountId = inputAccountFrom!!.accountId!!,
                                    transactionAccountTransferTo = inputAccountTo!!.accountId,
                                    transactionAmount = inputAmount!!
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
                                transactionAccountId = inputAccountFrom!!.accountId!!,
                                transactionBudgetId = inputBudget!!.budgetId,
                                transactionAmount = inputAmount ?: 0.0,
                                transactionType = 1,
                                transactionTime = OffsetDateTime.now()
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
                                transactionAccountId = inputAccountFrom!!.accountId!!,
                                transactionAmount = inputAmount ?: 0.0,
                                transactionType = 2,
                                transactionTime = OffsetDateTime.now()
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
                        else -> {
                            val transaction = Transaction(
                                transactionAccountId = inputAccountFrom!!.accountId!!,
                                transactionAmount = inputAmount ?: 0.0,
                                transactionAccountTransferTo = inputAccountTo!!.accountId,
                                transactionType = 3,
                                transactionTime = OffsetDateTime.now()
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
                val result = transactionRepository.delete(Transaction(transactionId = transaction!!.transactionId))
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

    suspend fun getTransactionById(transactionId: Long): Transaction {
        return transactionRepository.getTransactionById(transactionId)
    }

    enum class TransactionType { EXPENSE, INCOME, TRANSFER }

    sealed class AddEditTransactionEvent {
        data class ShowSnackbar(val msg: String, val length: Int) : AddEditTransactionEvent()
        data class NavigateBackWithAddResult(val result: Long) : AddEditTransactionEvent()
        data class NavigateBackWithEditResult(val result: Int) : AddEditTransactionEvent()
        data class NavigateBackWithDeleteResult(val result: Int) : AddEditTransactionEvent()
    }
}