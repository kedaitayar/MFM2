package io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.common

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.entity.Transaction
import io.github.kedaitayar.mfm.data.podata.AccountListAdapterData
import io.github.kedaitayar.mfm.databinding.FragmentTransferTransactionBinding
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.AddEditTransactionViewModel
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.add_transaction.AddTransactionChild
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.add_transaction.AddTransactionFragment
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.edit_transaction.EditTransactionChild
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.utils.AccountListArrayAdapter
import io.github.kedaitayar.mfm.util.SoftKeyboardManager.hideKeyboard
import io.github.kedaitayar.mfm.util.toStringOrBlank
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val ARG_TRANSACTION =
    "io.github.kedaitayar.mfm.ui.transaction.TransferTransactionFragment.TransactionId"

@AndroidEntryPoint
class TransferTransactionFragment : Fragment(R.layout.fragment_transfer_transaction),
                                    AddTransactionChild,
                                    EditTransactionChild {
    private val addEditTransactionViewModel: AddEditTransactionViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private var _binding: FragmentTransferTransactionBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTransferTransactionBinding.bind(view)

        setupAccountDropdown()
        setupDateInput()
        setupInputListener()
        setupInputErrorListener()

        if (addEditTransactionViewModel.transaction != null || addEditTransactionViewModel.quickTransaction != null) {
            setupEditTransactionValue()
        }
    }

    private fun setupInputErrorListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    addEditTransactionViewModel.errorAccountFrom.collect {
                        binding.textInputLayoutTransferFrom.error = it
                    }
                }
                launch {
                    addEditTransactionViewModel.errorAccountTo.collect {
                        binding.textInputLayoutTransferTo.error = it
                    }
                }
                launch {
                    addEditTransactionViewModel.errorAmount.collect {
                        binding.textInputLayoutAmount.error = it
                    }
                }
            }
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
            datePicker.show(parentFragmentManager, "date_picker_03")
        }
        datePicker.addOnPositiveButtonClickListener {
            val date = OffsetDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
            addEditTransactionViewModel.inputDate.value = date
            binding.textInputEditDate.setText(date.format(dateFormatter))
        }
    }

    private fun setupInputListener() {
        binding.apply {
            autoCompleteTransferFrom.setOnItemClickListener { parent, _, position, _ ->
                addEditTransactionViewModel.inputAccountFrom =
                    parent.getItemAtPosition(position) as AccountListAdapterData?
                addEditTransactionViewModel.errorAccountFrom.value = null
                addEditTransactionViewModel.errorAccountTo.value = null
            }
            autoCompleteTransferTo.setOnItemClickListener { parent, _, position, id ->
                addEditTransactionViewModel.inputAccountTo =
                    parent.getItemAtPosition(position) as AccountListAdapterData?
                addEditTransactionViewModel.errorAccountFrom.value = null
                addEditTransactionViewModel.errorAccountTo.value = null
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
                binding.autoCompleteTransferFrom.setText(it.accountName, false)
            }
        }
        addEditTransactionViewModel.accountTo.observe(viewLifecycleOwner) {
            it?.let {
                addEditTransactionViewModel.inputAccountTo = it
                binding.autoCompleteTransferTo.setText(it.accountName, false)
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
                binding.autoCompleteTransferFrom.setAdapter(adapter)

                val adapter2 = AccountListArrayAdapter(
                    requireContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    list2
                )
                binding.autoCompleteTransferTo.setAdapter(adapter2)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (parentFragment is AddTransactionFragment) {
            (parentFragment as AddTransactionFragment).setCurrentPage(this)
        }
        binding.apply {
            addEditTransactionViewModel.inputAccountFrom?.let {
                autoCompleteTransferFrom.setText(it.accountName, false)
            }
            addEditTransactionViewModel.inputAccountTo?.let {
                autoCompleteTransferTo.setText(it.accountName, false)
            }
            textInputEditAmount.setText(addEditTransactionViewModel.inputAmount?.toString())
            textInputEditNote.setText(addEditTransactionViewModel.inputNote)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onButtonAddClick() {
        addEditTransactionViewModel.onButtonAddClick(AddEditTransactionViewModel.TransactionType.TRANSFER)
    }

    override fun onButtonSaveClick() {
        addEditTransactionViewModel.onButtonSaveClick(AddEditTransactionViewModel.TransactionType.TRANSFER)
    }

    companion object {
        @JvmStatic
        fun newInstance(transaction: Transaction) =
            TransferTransactionFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_TRANSACTION, transaction)
                }
            }
    }
}