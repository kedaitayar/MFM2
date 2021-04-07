package io.github.kedaitayar.mfm.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["budgetName"], unique = true)]
)
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val budgetId: Long = 0,
    val budgetGoal: Double = 0.0,
    val budgetName: String = "",
    val budgetType: Int = 1,
)