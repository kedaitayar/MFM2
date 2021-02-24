package io.github.kedaitayar.mfm.ui.budget

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.kedaitayar.mfm.data.podata.BudgetListAdapterData
import io.github.kedaitayar.mfm.databinding.RecyclerViewItemBudgetingListBinding

class BudgetingListAdapter :
    ListAdapter<BudgetListAdapterData, BudgetingListAdapter.BudgetingListViewHolder>(
        BudgetingListDiffCallback()
    ) {
    private var budgetingAmountList = mutableMapOf<Int, String>()

    inner class BudgetingListViewHolder(private val binding: RecyclerViewItemBudgetingListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.textInputEditAmount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    //not use
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    //not use
                }

                override fun afterTextChanged(s: Editable?) {
                    budgetingAmountList[adapterPosition] = s.toString()
                }
            })
        }

        fun bind(item: BudgetListAdapterData?) {
            binding.apply {
                textViewBudget.text = item?.budgetName
                textViewGoal.text = item?.budgetGoal.toString()
                textInputEditAmount.setText(item?.budgetAllocation.toString())
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

    fun getBudgetingAmountList(): Map<Int, String> {
        return budgetingAmountList
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