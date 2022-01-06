package io.github.kedaitayar.mfm.data.podata

data class BudgetTrendGraphData(
    val budgetId: Long,
    val budgetGoal: Double,
    val budgetName: String,
    val budgetType: Int,
    val budgetTransactionMonth: Int,
    val budgetTransactionYear: Int,
    val budgetTransactionAmount: Double,
)
