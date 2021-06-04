package io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.podata.BudgetListAdapterData
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class BudgetListArrayAdapter(context: Context, resource: Int, private val objects: List<BudgetListAdapterData>) :
    ArrayAdapter<BudgetListAdapterData>(context, resource, objects) {
    val format = DecimalFormatSymbols().apply {
        groupingSeparator = ' '
    }
    val formatter = DecimalFormat(context.getString(R.string.currency_format)).apply {
        decimalFormatSymbols = format
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val currentItemView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.support_simple_spinner_dropdown_item, parent, false)
        val budget: BudgetListAdapterData? = getItem(position)
        val textView = currentItemView as TextView
        budget?.let {
            val budgetAvailableBalance = budget.budgetAllocation - budget.budgetUsed
            textView.text = "${budget.budgetName} (${
                context.getString(
                    R.string.currency_symbol,
                    formatter.format(budgetAvailableBalance)
                )
            })"
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

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null) {
                    if (results.count > 0) {
                        notifyDataSetChanged()
                    } else {
                        notifyDataSetInvalidated()
                    }
                }
            }

            override fun convertResultToString(resultValue: Any?): CharSequence {
                if (resultValue is BudgetListAdapterData) {
                    return resultValue.budgetName
                }
                return resultValue.toString()
            }
        }
    }
}