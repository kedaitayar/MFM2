package io.github.kedaitayar.mfm.ui.transaction

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.kedaitayar.mfm.data.podata.TransactionListAdapterData
import io.github.kedaitayar.mfm.databinding.RecyclerViewItemTransctionListBinding

class TransactionListAdapter :
    ListAdapter<TransactionListAdapterData, TransactionListAdapter.TransactionListViewHolder>(
        TransactionListDiffCallback()
    ) {
    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onPopupMenuButtonClick(
            transactionListAdapterData: TransactionListAdapterData,
            popupMenuButton: Button
        )
    }

    inner class TransactionListViewHolder(private val binding: RecyclerViewItemTransctionListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonPopupMenu.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener?.onPopupMenuButtonClick(getItem(adapterPosition), it as Button)
                }
            }
        }

        fun bind(item: TransactionListAdapterData) {
                binding.apply {
                    textViewTransactionAccount.text = item.transactionAccountName
                    textViewTransactionAmount.text = "RM ${item.transactionAmount}"
                    textViewTransactionDate.text = "${item.transactionTime?.dayOfMonth ?: 0}/${item.transactionTime?.monthValue ?: 0}/${item.transactionTime?.year ?: 0}"
                    when (item.transactionTypeId) {
                        1 -> {
                            textViewTransactionBudget.text = item.transactionBudgetName
                            textViewTransactionAccountTo.visibility = View.GONE
                            textViewTransactionAmount.setTextColor(Color.parseColor("#d50000"))
                        }
                        2 -> {
                            textViewTransactionBudget.text = item.transactionTypeName
                            textViewTransactionAccountTo.visibility = View.GONE
                            textViewTransactionAmount.setTextColor(Color.parseColor("#00c853"))
                        }
                        3 -> {
                            textViewTransactionBudget.text = item.transactionTypeName
                            textViewTransactionAccountTo.text = item.transactionAccountTransferToName
                            textViewTransactionAccountTo.visibility = View.VISIBLE
                            textViewTransactionAmount.setTextColor(Color.parseColor("#000000"))
                        }
                    }
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionListViewHolder {
        return TransactionListViewHolder(
            RecyclerViewItemTransctionListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TransactionListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
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