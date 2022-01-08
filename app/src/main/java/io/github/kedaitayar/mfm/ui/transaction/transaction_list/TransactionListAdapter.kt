package io.github.kedaitayar.mfm.ui.transaction.transaction_list

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
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
    private var colorGreen = 0
    private var colorRed = 0

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val typedValue = TypedValue()
        recyclerView.context.theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true)
        colorOnSurface = ContextCompat.getColor(recyclerView.context, typedValue.resourceId)
        recyclerView.context.theme.resolveAttribute(R.attr.greenOnSurface, typedValue, true)
        colorGreen = ContextCompat.getColor(recyclerView.context, typedValue.resourceId)
        recyclerView.context.theme.resolveAttribute(R.attr.redOnSurface, typedValue, true)
        colorRed = ContextCompat.getColor(recyclerView.context, typedValue.resourceId)
    }

    interface OnItemClickListener {
        fun onClick(
            transactionListAdapterData: TransactionListAdapterData,
            transactionCard: ConstraintLayout
        )
    }

    inner class TransactionListViewHolder(private val binding: RecyclerViewItemTransactionListBinding) :
        RecyclerView.ViewHolder(binding.root) {

//        init {
//            binding.root.setOnClickListener { view ->
//                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
//                    val currentItem = getItem(bindingAdapterPosition)
//                    currentItem?.let { item ->
//                        listener?.onClick(item, binding.root)
//                    }
//                }
//            }
//        }

        fun bind(item: TransactionListAdapterData) {
            binding.apply {
                root.transitionName = "edit_transaction_${item.transactionId}"
                root.setOnClickListener {
                    listener?.onClick(item, root)
                }

                textViewTransactionAccount.text = item.transactionAccountName
                textViewTransactionAmount.text = "RM ${item.transactionAmount}"
                textViewTransactionDate.text =
                    "${item.transactionTime?.dayOfMonth ?: 0}/${item.transactionTime?.monthValue ?: 0}/${item.transactionTime?.year ?: 0}"
                if (item.transactionNote.isNotEmpty()) {
                    textViewTransactionNote.visibility = View.VISIBLE
                    textViewTransactionNote.text = "- ${item.transactionNote}"
                } else {
                    textViewTransactionNote.visibility = View.GONE
                }
                when (item.transactionTypeId) {
                    1 -> {
                        textViewTransactionBudget.text = item.transactionBudgetName
                        textViewTransactionAccountTo.visibility = View.GONE
                        textViewTransactionAmount.setTextColor(colorRed)
                    }
                    2 -> {
                        textViewTransactionBudget.text = item.transactionTypeName
                        textViewTransactionAccountTo.visibility = View.GONE
                        textViewTransactionAmount.setTextColor(colorGreen)
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