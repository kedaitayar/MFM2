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
import io.github.kedaitayar.mfm.data.entity.Budget
import io.github.kedaitayar.mfm.data.podata.BudgetListAdapterData

class BudgetListArrayAdapter(context: Context, resource: Int, private val objects: List<Budget>) :
    ArrayAdapter<Budget>(context, resource, objects) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val currentItemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.support_simple_spinner_dropdown_item, parent, false)
        val budget = getItem(position)
        val textView = currentItemView as TextView
        budget?.let {
            textView.text = budget.budgetName
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
                if (resultValue is Budget) {
                    return resultValue.budgetName
                }
                return resultValue.toString()
            }
        }
    }
}