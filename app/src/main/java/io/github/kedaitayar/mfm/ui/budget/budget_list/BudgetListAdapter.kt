package io.github.kedaitayar.mfm.ui.budget.budget_list

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
import io.github.kedaitayar.mfm.util.toCurrency
import java.util.*

class BudgetListAdapter :
    ListAdapter<BudgetListAdapterData, BudgetListAdapter.BudgetListViewHolder>(
        BudgetListDiffCallback()
    ) {
    private var budgetType = 0
    private var listener: OnItemClickListener? = null
    private var green = 0
    private var yellow = 0
    private var red = 0
    private var onSurface = 0

    fun moveItem(fromPosition: Int, toPosition: Int) {
        val mutableCurrentList = currentList.toMutableList()
        Collections.swap(mutableCurrentList, fromPosition, toPosition)
        submitList(mutableCurrentList)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val typedValue = TypedValue()
        recyclerView.context.theme.resolveAttribute(R.attr.gGreen, typedValue, true)
        green = ContextCompat.getColor(recyclerView.context, typedValue.resourceId)
        recyclerView.context.theme.resolveAttribute(R.attr.gYellow, typedValue, true)
        yellow = ContextCompat.getColor(recyclerView.context, typedValue.resourceId)
        recyclerView.context.theme.resolveAttribute(R.attr.gRed, typedValue, true)
        red = ContextCompat.getColor(recyclerView.context, typedValue.resourceId)
        recyclerView.context.theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true)
        onSurface = ContextCompat.getColor(recyclerView.context, typedValue.resourceId)
    }

    interface OnItemClickListener {
        fun onPopupMenuButtonClick(
            budgetListAdapterData: BudgetListAdapterData,
            popupMenuButton: Button
        )

        fun onBudgetedPillClick(budgetListAdapterData: BudgetListAdapterData) {

        }
    }

    inner class BudgetListViewHolder(private val binding: RecyclerViewItemBudgetListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonMore.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    listener?.onPopupMenuButtonClick(getItem(bindingAdapterPosition), it as Button)
                }
            }
            binding.textViewBudgeted.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    listener?.onBudgetedPillClick(getItem(bindingAdapterPosition))
                }
            }
        }

        fun onDragHandler() {
            binding.root.isDragged = true
        }

        fun onClearViewHandler() {
            binding.root.isDragged = false
        }

        fun bind(item: BudgetListAdapterData) {
            binding.apply {
                textViewBudgetName.text = item.budgetName
                textViewBudgeted.text =
                    item.budgetAllocation.toCurrency(textViewBudgeted.context, false)
                val goalPercentage = when (budgetType) {
                    1 -> {
                        ((item.budgetAllocation / item.budgetGoal) * 100).toInt()
                    }
                    2 -> {
                        (((item.budgetAllocation + item.budgetTotalPrevAllocation) / item.budgetGoal) * 100).toInt()
                    }
                    else -> 100
                }
                textViewBudgeted.piePercent = goalPercentage
                if (goalPercentage >= 100) {
                    textViewBudgeted.bgColor = green
                } else {
                    textViewBudgeted.bgColor = yellow
                }
                when (budgetType) {
                    1 -> {
                        val availableBudget = item.budgetAllocation - item.budgetUsed
                        textViewAvailable.text =
                            availableBudget.toCurrency(textViewAvailable.context, false)
                        textViewAvailable.background.setTint(if (availableBudget >= 0) green else red)
                    }
                    2 -> {
                        val availableBudget =
                            item.budgetAllocation + item.budgetTotalPrevAllocation - item.budgetUsed
                        textViewAvailable.text = availableBudget.toString()
                        textViewAvailable.background.setTint(if (availableBudget >= 0) green else red)
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