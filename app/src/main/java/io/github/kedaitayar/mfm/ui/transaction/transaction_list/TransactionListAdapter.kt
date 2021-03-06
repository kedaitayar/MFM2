package io.github.kedaitayar.mfm.ui.transaction.transaction_list

import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.podata.TransactionListAdapterData
import io.github.kedaitayar.mfm.databinding.RecyclerViewItemTransactionListBinding

class TransactionListAdapter :
    PagingDataAdapter<TransactionListAdapterData, TransactionListAdapter.TransactionListViewHolder>(
        TransactionListDiffCallback()
    ) {
    private var listener: OnItemClickListener? = null
    private var colorOnSurface = 0

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val typedValue = TypedValue()
        recyclerView.context.theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true)
        colorOnSurface = ContextCompat.getColor(recyclerView.context, typedValue.resourceId)
    }

    interface OnItemClickListener {
        fun onPopupMenuButtonClick(
            transactionListAdapterData: TransactionListAdapterData,
            popupMenuButton: Button
        )
    }

    inner class TransactionListViewHolder(private val binding: RecyclerViewItemTransactionListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonPopupMenu.setOnClickListener { view ->
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    val currentItem = getItem(bindingAdapterPosition)
                    currentItem?.let { item ->
                        listener?.onPopupMenuButtonClick(item, view as Button)
                    }
                }
            }
        }

        fun bind(item: TransactionListAdapterData) {
            binding.apply {
                textViewTransactionAccount.text = item.transactionAccountName
                textViewTransactionAmount.text = "RM ${item.transactionAmount}"
                textViewTransactionDate.text =
                    "${item.transactionTime?.dayOfMonth ?: 0}/${item.transactionTime?.monthValue ?: 0}/${item.transactionTime?.year ?: 0}"
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
                        textViewTransactionAmount.setTextColor(colorOnSurface)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionListViewHolder {
        return TransactionListViewHolder(
            RecyclerViewItemTransactionListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TransactionListViewHolder, position: Int) {
        val currentItem = getItem(position)
        currentItem?.let {
            holder.bind(currentItem)
        }
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