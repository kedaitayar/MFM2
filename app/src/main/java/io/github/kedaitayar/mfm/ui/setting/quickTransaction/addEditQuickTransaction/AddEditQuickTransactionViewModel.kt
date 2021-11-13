package io.github.kedaitayar.mfm.ui.setting.quickTransaction.addEditQuickTransaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.entity.Budget
import io.github.kedaitayar.mfm.data.entity.QuickTransaction
import io.github.kedaitayar.mfm.data.podata.QuickTransactionListAdapterData
import io.github.kedaitayar.mfm.data.podata.toAccount
import io.github.kedaitayar.mfm.data.podata.toAccountTo
import io.github.kedaitayar.mfm.data.podata.toBudget
import io.github.kedaitayar.mfm.data.repository.BasicRepository
import io.github.kedaitayar.mfm.data.repository.TransactionRepository
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.AddEditTransactionViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditQuickTransactionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val basicRepository: BasicRepository,
    transactionRepository: TransactionRepository,
) : ViewModel() {
    private val addEditQuickTransactionEventChannel = Channel<AddEditQuickTransactionEvent>()
    val addEditQuickTransactionEvent = addEditQuickTransactionEventChannel.receiveAsFlow()
    val transaction = savedStateHandle.get<QuickTransactionListAdapterData>("quickTransaction")

    var inputName: String? = transaction?.quickTransactionName
    var inputAccountFrom: Account? = transaction?.toAccount()
    var inputBudget: Budget? = transaction?.toBudget()
    var inputAccountTo: Account? = transaction?.toAccountTo()
    var inputAmount: Double? = transaction?.transactionAmount
    var inputNote: String? = transaction?.transactionNote

    val allAccount = transactionRepository.getAllAccountFlow()
    val allBudget = transactionRepository.getAllBudgetFlow()

    fun onButtonAddClick(transactionType: AddEditTransactionViewModel.TransactionType) {
        viewModelScope.launch {
            when (transactionType) {
                AddEditTransactionViewModel.TransactionType.EXPENSE -> {
                    val quickTransaction =
                        QuickTransaction(
                            quickTransactionId = transaction?.quickTransactionId ?: 0,
                            quickTransactionName = inputName!!,
                            transactionAmount = inputAmount!!,
                            transactionType = 1,
                            transactionAccountId = inputAccountFrom!!.accountId,
                            transactionBudgetId = inputBudget!!.budgetId,
                            transactionNote = inputNote ?: "",
                        )
                    if (transaction == null) {
                        val result = basicRepository.insert(quickTransaction)
                        addEditQuickTransactionEventChannel.send(
                            AddEditQuickTransactionEvent.NavigateBackWithAddResult(
                                result
                            )
                        )
                    } else {
                        val result = basicRepository.update(quickTransaction)
                        addEditQuickTransactionEventChannel.send(
                            AddEditQuickTransactionEvent.NavigateBackWithEditResult(result)
                        )
                    }
                }
                AddEditTransactionViewModel.TransactionType.INCOME -> {
                    val quickTransaction =
                        QuickTransaction(
                            quickTransactionId = transaction?.quickTransactionId ?: 0,
                            quickTransactionName = inputName!!,
                            transactionAmount = inputAmount!!,
                            transactionType = 2,
                            transactionAccountId = inputAccountFrom!!.accountId,
                            transactionNote = inputNote ?: "",
                        )
                    if (transaction == null) {
                        val result = basicRepository.insert(quickTransaction)
                        addEditQuickTransactionEventChannel.send(
                            AddEditQuickTransactionEvent.NavigateBackWithAddResult(
                                result
                            )
                        )
                    } else {
                        val result = basicRepository.update(quickTransaction)
                        addEditQuickTransactionEventChannel.send(
                            AddEditQuickTransactionEvent.NavigateBackWithEditResult(result)
                        )
                    }
                }
                AddEditTransactionViewModel.TransactionType.TRANSFER -> {
                    val quickTransaction =
                        QuickTransaction(
                            quickTransactionId = transaction?.quickTransactionId ?: 0,
                            quickTransactionName = inputName!!,
                            transactionAmount = inputAmount!!,
                            transactionType = 3,
                            transactionAccountId = inputAccountFrom!!.accountId,
                            transactionAccountTransferTo = inputAccountTo!!.accountId,
                            transactionNote = inputNote ?: "",
                        )
                    if (transaction == null) {
                        val result = basicRepository.insert(quickTransaction)
                        addEditQuickTransactionEventChannel.send(
                            AddEditQuickTransactionEvent.NavigateBackWithAddResult(
                                result
                            )
                        )
                    } else {
                        val result = basicRepository.update(quickTransaction)
                        addEditQuickTransactionEventChannel.send(
                            AddEditQuickTransactionEvent.NavigateBackWithEditResult(result)
                        )
                    }
                }
            }
        }
    }

    fun onDelete() {
        if (transaction != null) {
            val quickTransaction =
                QuickTransaction(quickTransactionId = transaction.quickTransactionId)
            viewModelScope.launch {
                val result = basicRepository.delete(quickTransaction)
                addEditQuickTransactionEventChannel.send(
                    AddEditQuickTransactionEvent.NavigateBackWithDeleteResult(
                        result
                    )
                )
            }
        }
    }
}

enum class TransactionType { EXPENSE, INCOME, TRANSFER }

sealed class AddEditQuickTransactionEvent {
    data class NavigateBackWithAddResult(val result: Long) : AddEditQuickTransactionEvent()
    data class NavigateBackWithEditResult(val result: Int) : AddEditQuickTransactionEvent()
    data class NavigateBackWithDeleteResult(val result: Int) : AddEditQuickTransactionEvent()
}