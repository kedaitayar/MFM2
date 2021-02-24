package io.github.kedaitayar.mfm.ui.budget

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.kedaitayar.mfm.data.podata.AccountListAdapterData
import io.github.kedaitayar.mfm.data.podata.BudgetListAdapterData
import io.github.kedaitayar.mfm.databinding.RecyclerViewItemBudgetListBinding

private const val TAG = "BudgetListAdapter"

class BudgetListAdapter :
    ListAdapter<BudgetListAdapterData, BudgetListAdapter.BudgetListViewHolder>(
        BudgetListDiffCallback()
    ) {

    inner class BudgetListViewHolder(private val binding: RecyclerViewItemBudgetListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BudgetListAdapterData) {
            Log.i(TAG, "bind: $item")
            binding.apply {
                textViewBudgetName.text = item.budgetName
                textViewBudgeted.text = item.budgetAllocation.toString()
                textViewUsed.text = item.budgetUsed.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetListViewHolder {
        return BudgetListViewHolder(
            RecyclerViewItemBudgetListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BudgetListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

private class BudgetListDiffCallback : DiffUtil.ItemCallback<BudgetListAdapterData>() {

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