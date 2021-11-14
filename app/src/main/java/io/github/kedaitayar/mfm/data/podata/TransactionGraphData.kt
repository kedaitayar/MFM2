package io.github.kedaitayar.mfm.data.podata

data class TransactionGraphData(
    val transactionWeek: Int,
    val transactionAmount: Double,
    val transactionAmountPrevYear: Double,
    val transactionType: Int,
    val transactionAgeInWeek: Int,
)
