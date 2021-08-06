package io.github.kedaitayar.mfm.ui.budget.budgeting

import android.text.Editable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.kedaitayar.mfm.data.podata.BudgetListAdapterData
import io.github.kedaitayar.mfm.databinding.RecyclerViewItemBudgetingListBinding

class BudgetingListAdapter(private var listener: OnBudgetingListAdapterListener) :
    ListAdapter<BudgetListAdapterData, BudgetingListAdapter.BudgetingListViewHolder>(
        BudgetingListDiffCallback()
    ) {

    interface OnBudgetingListAdapterListener {
        fun onAfterTextChanged(item: BudgetListAdapterData, editable: Editable?)
    }

    inner class BudgetingListViewHolder(private val binding: RecyclerViewItemBudgetingListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.textInputEditAmount.doAfterTextChanged {
                listener.onAfterTextChanged(getItem(bindingAdapterPosition), it)
            }
        }

        fun bind(item: BudgetListAdapterData) {
            binding.apply {
                textViewBudget.text = item.budgetName
                textInputEditAmount.setText(item.budgetAllocation.toString())
                var goal = 0.0
                when (item.budgetTypeId) {
                    1L -> {
                        textViewGoal.text = item.budgetGoal.toString()
                        goal = item.budgetGoal
                    }
                    2L -> {
                        val remainingGoal = item.budgetGoal - item.budgetTotalPrevAllocation
                        textViewGoal.text = remainingGoal.toString()
                        goal = remainingGoal
                    }
                }
                textViewGoal.setOnClickListener {
                    textInputEditAmount.setText(goal.toString())
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetingListViewHolder {
        return BudgetingListViewHolder(
            RecyclerViewItemBudgetingListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BudgetingListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

}

private class BudgetingListDiffCallback : DiffUtil.ItemCallback<BudgetListAdapterData>() {
    override fun areItemsTheSame(
        oldItem: BudgetListAdapterData,
        newItem: BudgetListAdapterData
    ): Boolean {
        return oldItem.budgetId == newItem.budgetId
    }

    override fun areContentsTheSame(
        oldItem: BudgetListAdapterData,
        newItem: BudgetListAdapterData
    ): Boolean {
        return oldItem == newItem
    }
}