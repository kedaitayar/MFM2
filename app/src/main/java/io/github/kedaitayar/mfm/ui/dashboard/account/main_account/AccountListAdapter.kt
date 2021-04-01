package io.github.kedaitayar.mfm.ui.dashboard.account.main_account

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.kedaitayar.mfm.data.podata.AccountListAdapterData
import io.github.kedaitayar.mfm.databinding.RecyclerViewItemAccountListBinding
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class AccountListAdapter :
    ListAdapter<AccountListAdapterData, AccountListAdapter.AccountListViewHolder>(
        AccountListDiffCallback()
    ) {
    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onPopupMenuButtonClick(
            accountListAdapterData: AccountListAdapterData,
            popupMenuButton: Button
        )
    }

    inner class AccountListViewHolder(private val binding: RecyclerViewItemAccountListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonMore.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener?.onPopupMenuButtonClick(getItem(adapterPosition), it as Button)
                }
            }
        }

        fun bind(item: AccountListAdapterData) {
            binding.apply {
                textViewAccountName.text = item.accountName
                val balance =
                    (item.accountIncome - item.accountExpense - item.accountTransferOut + item.accountTransferIn)
                textViewAccountBalance.text =
                    "RM ${formater.format(balance)}"  //TODO: extract the string
            }
        }

        private val format = DecimalFormatSymbols().apply {
            groupingSeparator = ' '
        }   //TODO: extract the string
        private val formater = DecimalFormat("###,##0.00").apply {
            decimalFormatSymbols = format
        }  //TODO: extract the string
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountListViewHolder {
        return AccountListViewHolder(
            RecyclerViewItemAccountListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AccountListViewHolder, position: Int) {
        val item = getItem(position)
//        (holder as AccountListViewHolder).bind(item)
        holder.bind(item)
    }
}

private class AccountListDiffCallback : DiffUtil.ItemCallback<AccountListAdapterData>() {
    override fun areItemsTheSame(
        oldItem: AccountListAdapterData,
        newItem: AccountListAdapterData
    ): Boolean {
        return oldItem.accountId == newItem.accountId
    }

    override fun areContentsTheSame(
        oldItem: AccountListAdapterData,
        newItem: AccountListAdapterData
    ): Boolean {
        return oldItem == newItem
    }
}