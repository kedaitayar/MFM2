package io.github.kedaitayar.mfm.data.podata

data class BudgetTypeDropdownData(
    var budgetTypeId: Long? = null,
    var budgetTypeName: String = ""
){
    override fun toString(): String {
        return budgetTypeName
    }
}
