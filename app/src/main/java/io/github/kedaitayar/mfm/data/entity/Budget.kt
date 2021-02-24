package io.github.kedaitayar.mfm.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["budgetName"], unique = true)]
)
data class Budget(
    @PrimaryKey(autoGenerate = true)
    var budgetId: Long? = null,
    var budgetGoal: Double = 0.0,
    var budgetName: String = "",
    var budgetType: Int = 1,
)