package io.github.kedaitayar.mfm.data.podata

data class BudgetTransactionJoinTransaction(
    val budgetId: Long = 0,
    val budgetName: String = "",
    val budgetType: Int = 0,
    val budgetTransactionMonth: Int = 0,
    val budgetTransactionYear: Int = 0,
    val budgetTransactionAmount: Double = 0.0,
    val transactionAmount: Double = 0.0
)