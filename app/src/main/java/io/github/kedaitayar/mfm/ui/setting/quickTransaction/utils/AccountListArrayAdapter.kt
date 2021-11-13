package io.github.kedaitayar.mfm.ui.setting.quickTransaction.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.entity.Account

class AccountListArrayAdapter(context: Context, resource: Int, private val objects: List<Account>) :
    ArrayAdapter<Account>(context, resource, objects) {
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val currentItemView =
            convertView ?: LayoutInflater.from(context).inflate(
                R.layout.support_simple_spinner_dropdown_item,
                parent,
                false
            )
        val account = getItem(position)
        val textView = currentItemView as TextView
        account?.let {
            textView.text = account.accountName
        }
        return currentItemView
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val result = FilterResults()
                result.values = objects
                result.count = objects.size
                return result
            }

            override fun publishResults(
                constraint: CharSequence?,
                results: FilterResults?
            ) {
                if (results != null) {
                    if (results.count > 0) {
                        notifyDataSetChanged()
                    } else {
                        notifyDataSetInvalidated()
                    }
                }
            }

            override fun convertResultToString(resultValue: Any?): CharSequence {
                if (resultValue is Account) {
                    return resultValue.accountName
                }
                return resultValue.toString()
            }
        }
    }
}