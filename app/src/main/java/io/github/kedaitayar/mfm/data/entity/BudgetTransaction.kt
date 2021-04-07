package io.github.kedaitayar.mfm.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import io.github.kedaitayar.mfm.data.entity.Budget

@Entity(
    primaryKeys = ["budgetTransactionMonth", "budgetTransactionYear", "budgetTransactionBudgetId"],
    foreignKeys = [ForeignKey(
        entity = Budget::class,
        parentColumns = arrayOf("budgetId"),
        childColumns = arrayOf("budgetTransactionBudgetId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class BudgetTransaction(
    val budgetTransactionMonth: Int = -1,
    val budgetTransactionYear: Int = -1,
    val budgetTransactionAmount: Double = 0.0,
    val budgetTransactionBudgetId: Long = -1
)