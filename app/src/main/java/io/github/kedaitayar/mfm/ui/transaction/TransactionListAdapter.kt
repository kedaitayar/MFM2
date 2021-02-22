package io.github.kedaitayar.mfm.ui.transaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.kedaitayar.mfm.data.podata.TransactionListAdapterData
import io.github.kedaitayar.mfm.databinding.RecyclerViewItemTransctionListBinding

class TransactionListAdapter :
    ListAdapter<TransactionListAdapterData, TransactionListAdapter.TransactionListViewHodler>(
        TransactionListDiffCallback()
    ) {

    inner class TransactionListViewHodler(private val binding: RecyclerViewItemTransctionListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TransactionListAdapterData) {
                binding.apply {
                    textViewTransactionBudget.text = item.transactionTypeName
                    textViewTransactionAccount.text = item.transactionAccountName
                    textViewTransactionAmount.text = item.transactionAmount.toString()
                    textViewTransactionDate.text = "${item.transactionTime?.dayOfMonth ?: 0}/${item.transactionTime?.monthValue ?: 0}/${item.transactionTime?.year ?: 0}"
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionListViewHodler {
        return TransactionListViewHodler(
            RecyclerViewItemTransctionListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TransactionListViewHodler, position: Int) {
        holder.bind(getItem(position))
    }
}

private class TransactionListDiffCallback : DiffUtil.ItemCallback<TransactionListAdapterData>() {
    override fun areItemsTheSame(
        oldItem: TransactionListAdapterData,
        newItem: TransactionListAdapterData
    ): Boolean {
        return oldItem.transactionId == newItem.transactionId
    }

    override fun areContentsTheSame(
        oldItem: TransactionListAdapterData,
        newItem: TransactionListAdapterData
    ): Boolean {
        return oldItem == newItem
    }

}