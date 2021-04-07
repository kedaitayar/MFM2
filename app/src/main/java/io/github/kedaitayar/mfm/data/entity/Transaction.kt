package io.github.kedaitayar.mfm.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.OffsetDateTime

@Parcelize
@Entity(
    indices = [Index(value = ["transactionAccountId", "transactionBudgetId", "transactionAccountTransferTo"])],
    foreignKeys = [ForeignKey(
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
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val transactionId: Long = 0,
    val transactionAmount: Double = 0.0,
    val transactionTime: OffsetDateTime? = OffsetDateTime.now(),
    val transactionType: Int = 0,
    val transactionAccountId: Long = 0,
    val transactionBudgetId: Long? = null,
    val transactionAccountTransferTo: Long? = null
): Parcelable
