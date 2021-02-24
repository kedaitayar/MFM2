package io.github.kedaitayar.mfm.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["budgetTypeName"], unique = true)])
data class BudgetType(
    @PrimaryKey(autoGenerate = true)
    var budgetTypeId: Long? = null,
    var budgetTypeName: String = ""
)
