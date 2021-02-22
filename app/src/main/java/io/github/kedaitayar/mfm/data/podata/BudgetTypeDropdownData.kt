package io.github.kedaitayar.mfm.data.podata

import androidx.room.PrimaryKey

data class BudgetTypeDropdownData(
    var budgetTypeId: Long? = null,
    var budgetTypeName: String = ""
){
    override fun toString(): String {
        return budgetTypeName
    }
}
