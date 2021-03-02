package io.github.kedaitayar.mfm.data.podata

data class AccountTransactionBudgetData(
    var transactionAmount: Float?,
    var transactionType: Long,
    var transactionBudgetId: Long?,
    var budgetGoal: Float?,
    var budgetName: String?,
    var budgetType: Long?
)
