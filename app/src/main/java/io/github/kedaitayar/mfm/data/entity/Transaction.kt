package io.github.kedaitayar.mfm.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

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
    var transactionId: Long? = null,
    var transactionAmount: Double = 0.0,
    var transactionTime: OffsetDateTime? = OffsetDateTime.now(),
    var transactionType: Int = 0,
    var transactionAccountId: Long = 0,
    var transactionBudgetId: Long? = null,
    var transactionAccountTransferTo: Long? = null
)
