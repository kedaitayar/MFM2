package io.github.kedaitayar.mfm.data.podata

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.OffsetDateTime

@Parcelize
data class TransactionListAdapterData(
    var transactionId: Long = 0,
    var transactionAmount: Double = 0.0,
    var transactionTime: OffsetDateTime? = null,
    var transactionTypeId: Int = 0,
    var transactionTypeName: String = "",
    var transactionAccountId: Long = 0,
    var transactionBudgetId: Long? = null,
    var transactionAccountTransferTo: Long? = null,
    var transactionAccountName: String = "",
    var transactionBudgetName: String? = null,
    var transactionAccountTransferToName: String? = null,
    var transactionNote: String = ""
) : Parcelable
