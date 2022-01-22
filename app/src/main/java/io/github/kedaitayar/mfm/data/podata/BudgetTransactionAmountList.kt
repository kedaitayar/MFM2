package io.github.kedaitayar.mfm.data.podata

data class BudgetTransactionAmountList(
    val transactionAmount: Float = 0f,
    val budgetId: Long?,
    val budgetGoal: Float?,
    val budgetName: String?,
    val budgetType: Long?,
    val totalTransactionAmount: Float = 0f
)
