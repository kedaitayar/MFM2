package io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.entity.Budget
import io.github.kedaitayar.mfm.data.entity.Transaction
import io.github.kedaitayar.mfm.databinding.FragmentExpenseTransactionBinding
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.AddEditTransactionViewModel
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.add_transaction.AddTransactionChild
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.add_transaction.AddTransactionFragment
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.edit_transaction.EditTransactionChild
import io.github.kedaitayar.mfm.util.SoftKeyboardManager.hideKeyboard
import kotlinx.coroutines.*
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private const val ARG_TRANSACTION =
    "io.github.kedaitayar.mfm.ui.transaction.ExpenseTransactionFragment.Transaction"

@AndroidEntryPoint
class ExpenseTransactionFragment : Fragment(R.layout.fragment_expense_transaction),
    AddTransactionChild, EditTransactionChild {
    private val addEditTransactionViewModel: AddEditTransactionViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private var _binding: FragmentExpenseTransactionBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentExpenseTransactionBinding.bind(view)

        setupAccountDropdown()
        setupBudgetDropdown()
        setupDateInput()
        setupInputListener()

        if (addEditTransactionViewModel.transaction != null) {
            setupEditTransactionValue()
        }
    }

    private fun setupDateInput() {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        binding.textInputEditDate.setText(addEditTransactionViewModel.inputDate.format(dateFormatter))

        binding.textInputEditDate.setOnClickListener {
            hideKeyboard()
            datePicker.show(parentFragmentManager, "date_picker_01")
        }
        datePicker.addOnPositiveButtonClickListener {
            val instant = Instant.ofEpochMilli(it)
            val date = instant.atOffset(ZoneOffset.UTC)
            addEditTransactionViewModel.inputDate = date
            binding.textInputEditDate.setText(date.format(dateFormatter))
        }
    }

    private fun setupInputListener() {
        binding.apply {
            autoCompleteAccount.setOnItemClickListener { parent, view, position, id ->
                addEditTransactionViewModel.inputAccountFrom = parent.getItemAtPosition(position) as Account?
            }
            autoCompleteBudget.setOnItemClickListener { parent, view, position, id ->
                addEditTransactionViewModel.inputBudget = parent.getItemAtPosition(position) as Budget?
            }
            textInputEditAmount.addTextChangedListener {
                addEditTransactionViewModel.inputAmount = it.toString().toDoubleOrNull()
            }
        }
    }

    @ExperimentalCoroutinesApi
    private fun setupEditTransactionValue() {
        addEditTransactionViewModel.accountFrom.observe(viewLifecycleOwner) {
            it?.let {
                addEditTransactionViewModel.inputAccountFrom = it
                binding.autoCompleteAccount.setText(it.accountName, false)
            }
        }
        addEditTransactionViewModel.budget.observe(viewLifecycleOwner) {
            it?.let {
                addEditTransactionViewModel.inputBudget = it
                binding.autoCompleteBudget.setText(it.budgetName, false)
            }
        }
        addEditTransactionViewModel.transaction?.transactionAmount?.let {
            addEditTransactionViewModel.inputAmount = it
            binding.textInputEditAmount.setText(it.toString())
        }
//        addEditTransactionViewModel.transaction.observe(viewLifecycleOwner) {
//            it?.let {
//                addEditTransactionViewModel.inputAmount = it.transactionAmount
//                binding.textInputEditAmount.setText(it.transactionAmount.toString())
//            }
//        }
    }

    private fun setupAccountDropdown() {
        addEditTransactionViewModel.allAccount.observe(viewLifecycleOwner) { list ->
            list?.let { list2 ->
                val adapter = object : ArrayAdapter<Account>(
                    requireContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    list2
                ) {
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val currentItemView = convertView ?: LayoutInflater.from(context)
                            .inflate(R.layout.support_simple_spinner_dropdown_item, parent, false)
                        val account: Account? = getItem(position)
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
                                result.values = list2
                                result.count = list2.size
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
                                if (resultValue is Account) {
                                    return resultValue.accountName
                                }
                                return resultValue.toString()
                            }
                        }
                    }
                }
                binding.autoCompleteAccount.setAdapter(adapter)
            }
        }
    }

    private fun setupBudgetDropdown() {
        addEditTransactionViewModel.allBudget.observe(viewLifecycleOwner) { list ->
            list?.let { list2 ->
                val adapter = object : ArrayAdapter<Budget>(
                    requireContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    list2
                ) {

                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val currentItemView = convertView ?: LayoutInflater.from(context)
                            .inflate(R.layout.support_simple_spinner_dropdown_item, parent, false)
                        val budget: Budget? = getItem(position)
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
                                result.values = list2
                                result.count = list2.size
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
                binding.autoCompleteBudget.setAdapter(adapter)
            }
        }
    }

    override fun onButtonAddClick() {
        addEditTransactionViewModel.onButtonAddClick(AddEditTransactionViewModel.TransactionType.EXPENSE)
    }

    override fun onButtonSaveClick() {
        addEditTransactionViewModel.onButtonSaveClick(AddEditTransactionViewModel.TransactionType.EXPENSE)
    }

    override fun onResume() {
        super.onResume()
        if (parentFragment is AddTransactionFragment) {
            (parentFragment as AddTransactionFragment).setCurrentPage(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(transaction: Transaction) =
            ExpenseTransactionFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_TRANSACTION, transaction)
                }
            }
    }
}