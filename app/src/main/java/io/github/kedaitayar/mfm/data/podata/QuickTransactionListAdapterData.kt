package io.github.kedaitayar.mfm.data.podata

import android.os.Parcelable
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.entity.Budget
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuickTransactionListAdapterData(
    var quickTransactionId: Long = 0,
    var quickTransactionName: String = "",
    var transactionAmount: Double = 0.0,
    var transactionType: Int = 0,
    var transactionTypeName: String = "",
    var transactionAccountId: Long = 0,
    var transactionAccountName: String = "",
    var transactionBudgetId: Long? = null,
    var transactionBudgetName: String? = null,
    var transactionAccountTransferTo: Long? = null,
    var transactionAccountTransferToName: String? = null,
    val transactionNote: String = ""
) : Parcelable

fun QuickTransactionListAdapterData.toAccount() =
    Account(accountId = transactionAccountId, accountName = transactionAccountName)

fun QuickTransactionListAdapterData.toAccountTo(): Account? {
    transactionAccountTransferTo ?: return null
    transactionAccountTransferToName ?: return null
    return Account(
        accountId = transactionAccountTransferTo!!,
        accountName = transactionAccountTransferToName!!
    )
}

fun QuickTransactionListAdapterData.toBudget(): Budget? {
    transactionBudgetId ?: return null
    transactionBudgetName ?: return null
    return Budget(budgetId = transactionBudgetId!!, budgetName = transactionBudgetName!!)
}