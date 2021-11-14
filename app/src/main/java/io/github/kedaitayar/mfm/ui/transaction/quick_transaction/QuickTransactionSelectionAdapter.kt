package io.github.kedaitayar.mfm.ui.transaction.quick_transaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.entity.QuickTransaction
import io.github.kedaitayar.mfm.databinding.RecyclerViewItemQuickTransactionSelectionBinding

class QuickTransactionSelectionAdapter :
    ListAdapter<QuickTransaction, QuickTransactionSelectionAdapter.QuickTransactionSelectionViewHolder>(
        QuickSelectionDiffCallback()
    ) {
    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onClick(item: QuickTransaction)
    }

    inner class QuickTransactionSelectionViewHolder(private val binding: RecyclerViewItemQuickTransactionSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: QuickTransaction) {
            binding.apply {
                root.setOnClickListener {
                    listener?.onClick(item)
                }
                textViewQuickTransactionName.text = item.quickTransactionName
                when (item.transactionType) {
                    1 -> {
                        imageViewTransactionType.setImageResource(R.drawable.ic_wallet_out_24)
                    }
                    2 -> {
                        imageViewTransactionType.setImageResource(R.drawable.ic_wallet_in_24)
                    }
                    3 -> {
                        imageViewTransactionType.setImageResource(R.drawable.ic_wallet_switch_24)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): QuickTransactionSelectionViewHolder {
        return QuickTransactionSelectionViewHolder(
            RecyclerViewItemQuickTransactionSelectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: QuickTransactionSelectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}

private class QuickSelectionDiffCallback : DiffUtil.ItemCallback<QuickTransaction>() {
    override fun areItemsTheSame(oldItem: QuickTransaction, newItem: QuickTransaction): Boolean {
        return oldItem.quickTransactionId == newItem.quickTransactionId
    }

    override fun areContentsTheSame(oldItem: QuickTransaction, newItem: QuickTransaction): Boolean {
        return oldItem == newItem
    }
}