package io.github.kedaitayar.mfm.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = arrayOf("accountId"),
            childColumns = arrayOf("transactionAccountId"),
            onDelete = ForeignKey.CASCADE
        ), ForeignKey(
            entity = Budget::class,
            parentColumns = arrayOf("budgetId"),
            childColumns = arrayOf("transactionBudgetId"),
            onDelete = ForeignKey.CASCADE
        ), ForeignKey(
            entity = Account::class,
            parentColumns = arrayOf("accountId"),
            childColumns = arrayOf("transactionAccountTransferTo"),
            onDelete = ForeignKey.CASCADE
        )]
)
data class QuickTransaction(
    @PrimaryKey(autoGenerate = true)
    var quickTransactionId: Long = 0,
    var quickTransactionName: String = "",
    var transactionAmount: Double = 0.0,
    var transactionType: Int = 0,
    var transactionAccountId: Long = 0,
    var transactionBudgetId: Long? = null,
    var transactionAccountTransferTo: Long? = null,
    val transactionNote: String = ""
) : Parcelable
