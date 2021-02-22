package io.github.kedaitayar.mfm2.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.*

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
    var transactionTime: OffsetDateTime? = null,
    var transactionType: Int = 0,
    var transactionAccountId: Long = 0,
    var transactionBudgetId: Long? = null,
    var transactionAccountTransferTo: Long? = null
)
