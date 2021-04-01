package io.github.kedaitayar.mfm.ui.dashboard.account.account_detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.utils.ColorTemplate
import io.github.kedaitayar.mfm.data.podata.AccountTransactionBudgetData
import io.github.kedaitayar.mfm.databinding.RecyclerViewItemAccountBudgetChartListBinding
import java.util.ArrayList

class AccountBudgetChartListAdapter :
    ListAdapter<AccountTransactionBudgetData, AccountBudgetChartListAdapter.AccountBudgetChartListViewHolder>(
        AccountBudgetChartListDiffCallback()
    ) {
    private var totalTransactionAmount = 0.0
    private val colors = ArrayList<Int>()

    init {
        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)

        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)

        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)

        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)

        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)

        colors.add(ColorTemplate.getHoloBlue())
    }

    inner class AccountBudgetChartListViewHolder(private val binding: RecyclerViewItemAccountBudgetChartListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AccountTransactionBudgetData, position: Int) {
            binding.budgetColor.background.setTint(colors[position])
            binding.textViewBudget.text = item.budgetName
            binding.textViewTransactionAmount.text = "${item.transactionAmount}"
            binding.textViewPercentage.text =
                "${item.transactionAmount?.div(totalTransactionAmount)?.times(100)?.toInt() ?: 0}%"
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AccountBudgetChartListViewHolder {
        return AccountBudgetChartListViewHolder(
            RecyclerViewItemAccountBudgetChartListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AccountBudgetChartListViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    fun setTotalTransactionAmount(amount: Double) {
        this.totalTransactionAmount = amount
    }
}

private class AccountBudgetChartListDiffCallback :
    DiffUtil.ItemCallback<AccountTransactionBudgetData>() {
    override fun areItemsTheSame(
        oldItem: AccountTransactionBudgetData,
        newItem: AccountTransactionBudgetData
    ): Boolean {
        return oldItem.transactionBudgetId == newItem.transactionBudgetId
    }

    override fun areContentsTheSame(
        oldItem: AccountTransactionBudgetData,
        newItem: AccountTransactionBudgetData
    ): Boolean {
        return oldItem == newItem
    }
}