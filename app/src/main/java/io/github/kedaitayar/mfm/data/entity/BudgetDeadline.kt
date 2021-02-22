package io.github.kedaitayar.mfm2.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.OffsetDateTime

@Entity
data class BudgetDeadline(
    @PrimaryKey
    val budgetId: Long? = null,
    val budgetDeadline: OffsetDateTime? = null
)
