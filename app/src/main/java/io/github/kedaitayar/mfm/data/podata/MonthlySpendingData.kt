package io.github.kedaitayar.mfm.data.podata

import java.time.OffsetDateTime

data class MonthlySpendingData(
    val month: OffsetDateTime,
    val monthSpending: Double = 0.0,
)
