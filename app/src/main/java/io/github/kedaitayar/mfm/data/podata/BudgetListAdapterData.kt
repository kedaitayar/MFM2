package io.github.kedaitayar.mfm.data.podata

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BudgetListAdapterData(
    var budgetId: Long = 0,
    var budgetPosition: Long = 0,
    var budgetName: String = "",
    var budgetAllocation: Double = 0.0,
    var budgetGoal: Double = 0.0,
    var budgetUsed: Double = 0.0,
    var budgetTypeId: Long = 0,
    var budgetTypeName: String = "",
    var budgetTotalPrevAllocation: Double = 0.0
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BudgetListAdapterData

        if (budgetId != other.budgetId) return false
        if (budgetName != other.budgetName) return false
        if (budgetAllocation != other.budgetAllocation) return false
        if (budgetGoal != other.budgetGoal) return false
        if (budgetUsed != other.budgetUsed) return false
        if (budgetTypeId != other.budgetTypeId) return false
        if (budgetTypeName != other.budgetTypeName) return false
        if (budgetTotalPrevAllocation != other.budgetTotalPrevAllocation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = budgetId.hashCode()
        result = 31 * result + budgetName.hashCode()
        result = 31 * result + budgetAllocation.hashCode()
        result = 31 * result + budgetGoal.hashCode()
        result = 31 * result + budgetUsed.hashCode()
        result = 31 * result + budgetTypeId.hashCode()
        result = 31 * result + budgetTypeName.hashCode()
        result = 31 * result + budgetTotalPrevAllocation.hashCode()
        return result
    }

}
