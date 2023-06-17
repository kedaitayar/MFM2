package io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.common

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.entity.Transaction
import io.github.kedaitayar.mfm.data.podata.AccountListAdapterData
import io.github.kedaitayar.mfm.data.podata.BudgetListAdapterData
import io.github.kedaitayar.mfm.databinding.FragmentExpenseTransactionBinding
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.AddEditTransactionViewModel
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.add_transaction.AddTransactionChild
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.add_transaction.AddTransactionFragment
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.edit_transaction.EditTransactionChild
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.utils.AccountListArrayAdapter
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.utils.BudgetListArrayAdapter
import io.github.kedaitayar.mfm.util.SoftKeyboardManager.hideKeyboard
import io.github.kedaitayar.mfm.util.safeCollection
import io.github.kedaitayar.mfm.util.toStringOrBlank
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
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
        setupInputErrorListener()

        if (addEditTransactionViewModel.transaction != null || addEditTransactionViewModel.quickTransaction != null) {
            setupEditTransactionValue()
        }
    }

    private fun setupInputErrorListener() {
        addEditTransactionViewModel.errorAccountFrom.safeCollection(viewLifecycleOwner) {
            binding.textInputLayoutAccount.error = it
        }
        addEditTransactionViewModel.errorBudget.safeCollection(viewLifecycleOwner) {
            binding.textInputLayoutBudget.error = it
        }
        addEditTransactionViewModel.errorAmount.safeCollection(viewLifecycleOwner) {
            binding.textInputLayoutAmount.error = it
        }
    }

    private fun setupDateInput() {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        binding.textInputEditDate.setText(
            addEditTransactionViewModel.inputDate.value.format(
                dateFormatter
            )
        )

        binding.textInputEditDate.setOnClickListener {
            hideKeyboard()
            datePicker.show(parentFragmentManager, "date_picker_01")
        }
        datePicker.addOnPositiveButtonClickListener {
            val date = OffsetDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
            addEditTransactionViewModel.inputDate.value = date
            binding.textInputEditDate.setText(date.format(dateFormatter))
        }
    }

    private fun setupInputListener() {
        binding.apply {
            autoCompleteAccount.setOnItemClickListener { parent, _, position, _ ->
                addEditTransactionViewModel.inputAccountFrom =
                    parent.getItemAtPosition(position) as AccountListAdapterData?
                addEditTransactionViewModel.errorAccountFrom.value = null
            }
            autoCompleteBudget.setOnItemClickListener { parent, _, position, _ ->
                addEditTransactionViewModel.inputBudget =
                    parent.getItemAtPosition(position) as BudgetListAdapterData?
                addEditTransactionViewModel.errorBudget.value = null
            }
            textInputEditAmount.addTextChangedListener {
                addEditTransactionViewModel.inputAmount = it.toString().toDoubleOrNull()
                addEditTransactionViewModel.errorAmount.value = null
            }
            textInputEditNote.addTextChangedListener {
                addEditTransactionViewModel.inputNote = it.toStringOrBlank()
            }
        }
    }

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
        addEditTransactionViewModel.quickTransaction?.transactionAmount?.let {
            addEditTransactionViewModel.inputAmount = it
            binding.textInputEditAmount.setText(it.toString())
        }
        addEditTransactionViewModel.transaction?.transactionNote?.let {
            addEditTransactionViewModel.inputNote = it
            binding.textInputEditNote.setText(it)
        }
        addEditTransactionViewModel.quickTransaction?.transactionNote?.let {
            addEditTransactionViewModel.inputNote = it
            binding.textInputEditNote.setText(it)
        }
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        addEditTransactionViewModel.transaction?.transactionTime?.let {
            addEditTransactionViewModel.inputDate.value = it
            binding.textInputEditDate.setText(it.format(dateFormatter))
        }
    }

    private fun setupAccountDropdown() {
        addEditTransactionViewModel.allAccount.observe(viewLifecycleOwner) { list ->
            list?.let { list2 ->
                val adapter = AccountListArrayAdapter(
                    requireContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    list2
                )
                binding.autoCompleteAccount.setAdapter(adapter)
            }
        }
    }

    private fun setupBudgetDropdown() {
        addEditTransactionViewModel.allBudget.observe(viewLifecycleOwner) { list ->
            list?.let { list2 ->
                val adapter = BudgetListArrayAdapter(
                    requireContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    list2
                )
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
        binding.apply {
            addEditTransactionViewModel.inputAccountFrom?.let {
                autoCompleteAccount.setText(it.accountName, false)
            }
            addEditTransactionViewModel.inputBudget?.let {
                autoCompleteBudget.setText(it.budgetName, false)
            }
            textInputEditAmount.setText(addEditTransactionViewModel.inputAmount?.toString())
            textInputEditNote.setText(addEditTransactionViewModel.inputNote)
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