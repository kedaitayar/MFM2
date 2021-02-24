package io.github.kedaitayar.mfm.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity(
    foreignKeys = [ForeignKey(
        entity = Budget::class,
        parentColumns = arrayOf("budgetId"),
        childColumns = arrayOf("budgetId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class BudgetDeadline(
    @PrimaryKey
    val budgetId: Long? = null,
    var budgetDeadline: OffsetDateTime? = null
)
