package io.github.kedaitayar.mfm.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["budgetTypeName"], unique = true)])
data class BudgetType(
    @PrimaryKey(autoGenerate = true)
    val budgetTypeId: Long = 0,
    val budgetTypeName: String = ""
)
