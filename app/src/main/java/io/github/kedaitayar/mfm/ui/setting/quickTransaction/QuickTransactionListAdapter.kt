package io.github.kedaitayar.mfm.ui.setting.quickTransaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.kedaitayar.mfm.data.podata.QuickTransactionListAdapterData
import io.github.kedaitayar.mfm.databinding.RecyclerViewItemQuickTransactionMainListBinding

class QuickTransactionListAdapter :
    ListAdapter<QuickTransactionListAdapterData, QuickTransactionListAdapter.QuickTransactionListViewHolder>(
        QuickSelectionDiffCallback()
    ) {
    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onClick(
            quickTransactionListAdapterData: QuickTransactionListAdapterData,
        )
    }

    inner class QuickTransactionListViewHolder(private val binding: RecyclerViewItemQuickTransactionMainListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: QuickTransactionListAdapterData) {
            binding.apply {
                root.setOnClickListener {
                    listener?.onClick(item)
                }
                textViewQuickTransactionName.text = item.quickTransactionName
                textViewQuickTransactionAmount.text = item.transactionAmount.toString()
                textViewQuickTransactionBudget.text = item.transactionBudgetName
                textViewQuickTransactionAccount.text = item.transactionAccountName
                textViewQuickTransactionAccountTo.text = item.transactionAccountTransferToName
                textViewQuickTransactionType.text = item.transactionTypeName
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): QuickTransactionListViewHolder {
        return QuickTransactionListViewHolder(
            RecyclerViewItemQuickTransactionMainListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: QuickTransactionListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}

private class QuickSelectionDiffCallback :
    DiffUtil.ItemCallback<QuickTransactionListAdapterData>() {
    override fun areItemsTheSame(
        oldItem: QuickTransactionListAdapterData,
        newItem: QuickTransactionListAdapterData
    ): Boolean {
        return oldItem.quickTransactionId == newItem.quickTransactionId
    }

    override fun areContentsTheSame(
        oldItem: QuickTransactionListAdapterData,
        newItem: QuickTransactionListAdapterData
    ): Boolean {
        return oldItem == newItem
    }
}