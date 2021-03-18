package io.github.kedaitayar.mfm.ui.budget

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.podata.BudgetListAdapterData
import io.github.kedaitayar.mfm.databinding.RecyclerViewItemBudgetListBinding

class BudgetListAdapter :
    ListAdapter<BudgetListAdapterData, BudgetListAdapter.BudgetListViewHolder>(
        BudgetListDiffCallback()
    ) {
    private var budgetType = 0
    private var listener: OnItemClickListener? = null
    private var green = 0
    private var yellow = 0
    private var red = 0

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val typedValue = TypedValue()
        recyclerView.context.theme.resolveAttribute(R.attr.gGreen, typedValue, true)
        green = ContextCompat.getColor(recyclerView.context, typedValue.resourceId)
        recyclerView.context.theme.resolveAttribute(R.attr.gYellow, typedValue, true)
        yellow = ContextCompat.getColor(recyclerView.context, typedValue.resourceId)
        recyclerView.context.theme.resolveAttribute(R.attr.gRed, typedValue, true)
        red = ContextCompat.getColor(recyclerView.context, typedValue.resourceId)
    }

    interface OnItemClickListener {
        fun onPopupMenuButtonClick(
            budgetListAdapterData: BudgetListAdapterData,
            popupMenuButton: Button
        )
    }

    inner class BudgetListViewHolder(private val binding: RecyclerViewItemBudgetListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonMore.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener?.onPopupMenuButtonClick(getItem(adapterPosition), it as Button)
                }
            }
        }

        fun bind(item: BudgetListAdapterData) {
            binding.apply {
                textViewBudgetName.text = item.budgetName
                textViewBudgeted.text = item.budgetAllocation.toString()
                val goalPercentage = ((item.budgetAllocation / item.budgetGoal) * 100).toInt()
                textViewBudgeted.piePercent = goalPercentage
                if (goalPercentage >= 100) {
                    textViewBudgeted.bgColor = green
                } else {
                    textViewBudgeted.bgColor = yellow
                }
                if (item.budgetUsed <= item.budgetAllocation) {
                    textViewAvailable.background.setTint(green)
                } else {
                    textViewAvailable.background.setTint(red)
                }
                when (budgetType) {
                    1 -> {
                        textViewAvailable.text = (item.budgetAllocation - item.budgetUsed).toString()
                    }
                    2 -> {
                        textViewAvailable.text = (item.budgetAllocation + item.budgetTotalPrevAllocation - item.budgetUsed).toString()
                    }
                }

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

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun setBudgetType(budgetTypeId: Int) {
        this.budgetType = budgetTypeId
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