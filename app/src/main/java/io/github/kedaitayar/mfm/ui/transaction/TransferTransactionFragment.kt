package io.github.kedaitayar.mfm.ui.transaction

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentTransferTransactionBinding
import io.github.kedaitayar.mfm.util.SoftKeyboardManager.hideKeyboard
import io.github.kedaitayar.mfm.viewmodels.TransactionViewModel
import io.github.kedaitayar.mfm.data.entity.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime

private const val TAG = "TransferTransactionFrag"
private const val ARG_TRANSACTION_ID =
    "io.github.kedaitayar.mfm.ui.transaction.TransferTransactionFragment.TransactionId"

@AndroidEntryPoint
class TransferTransactionFragment : Fragment(R.layout.fragment_transfer_transaction), AddTransactionChild, EditTransactionChild {
    private val transactionViewModel: TransactionViewModel by viewModels()
    private var _binding: FragmentTransferTransactionBinding? = null
    private val binding get() = _binding!!
    private var transactionId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            transactionId = it.getLong(ARG_TRANSACTION_ID, -1L)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransferTransactionBinding.inflate(inflater, container, false)
        context ?: return binding.root

        setupAccountFromDropdown()
        setupAccountToDropdown()

        if (transactionId != -1L) {
            setupEditTransactionValue()
        }
        return binding.root
    }

    private fun setupEditTransactionValue() {
        CoroutineScope(Dispatchers.IO).launch {
            val transaction = transactionViewModel.getTransactionById(transactionId)
            withContext(Dispatchers.Main) {
                transactionViewModel.allAccount.observe(viewLifecycleOwner, Observer { list ->
                    binding.autoCompleteTransferFrom.setText(list.firstOrNull { it.accountId == transaction.transactionAccountId }?.accountName)
                    binding.autoCompleteTransferTo.setText(list.firstOrNull { it.accountId == transaction.transactionAccountTransferTo }?.accountName)
                })
                binding.textInputEditAmount.setText(transaction.transactionAmount.toString())
            }
        }
    }

    private fun setupAccountFromDropdown() {
        transactionViewModel.allAccount.observe(viewLifecycleOwner, Observer { list ->
            list?.let { list2 ->
                val items: List<String> = list2.map { it.accountName }
                val adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    items
                )
                binding.autoCompleteTransferFrom.setAdapter(adapter)
            }
        })
    }

    private fun setupAccountToDropdown() {
        transactionViewModel.allAccount.observe(viewLifecycleOwner, Observer { list ->
            list?.let { list2 ->
                val items: List<String> = list2.map { it.accountName }
                val adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    items
                )
                binding.autoCompleteTransferTo.setAdapter(adapter)
            }
        })
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

    override fun onButtonAddClick() {
        val accountFromDropdown = binding.autoCompleteTransferFrom.text.toString()
        val accountToDropdown = binding.autoCompleteTransferTo.text.toString()
        val transactionAmount = binding.textInputEditAmount.text.toString()
        if (accountFromDropdown.isNullOrBlank() || accountToDropdown.isNullOrBlank() || transactionAmount.isNullOrBlank()) {
            // TODO: notify user input validation error
        } else {
            val accountFromDropdown = transactionViewModel.allAccount.value?.let { list ->
                list.find { it.accountName == accountFromDropdown }
            }
            val accountToDropdown = transactionViewModel.allAccount.value?.let { list ->
                list.find { it.accountName == accountToDropdown }
            }
            if (accountFromDropdown != null) {
                if (accountToDropdown != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val transaction = Transaction(
                            transactionAccountId = accountFromDropdown.accountId!!,
                            transactionAccountTransferTo = accountToDropdown.accountId,
                            transactionAmount = binding.textInputEditAmount.text.toString()
                                .toDouble(),
                            transactionType = 3,
                            transactionTime = OffsetDateTime.now()
                        )
                        transactionViewModel.insert(transaction)
                    }
                    hideKeyboard()
                    findNavController().navigateUp()
                }

            }
        }
    }

    override fun onButtonSaveClick(transaction: Transaction) {
        val accountFromDropdown = binding.autoCompleteTransferFrom.text.toString()
        val accountToDropdown = binding.autoCompleteTransferTo.text.toString()
        val transactionAmount = binding.textInputEditAmount.text.toString()
        if (accountFromDropdown.isNullOrBlank() || accountToDropdown.isNullOrBlank() || transactionAmount.isNullOrBlank()) {
            // TODO: notify user input validation error
        } else {
            val accountFromDropdown = transactionViewModel.allAccount.value?.let { list ->
                list.find { it.accountName == accountFromDropdown }
            }
            val accountToDropdown = transactionViewModel.allAccount.value?.let { list ->
                list.find { it.accountName == accountToDropdown }
            }
            if (accountFromDropdown != null) {
                if (accountToDropdown != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val transaction = Transaction(
                            transactionId = transaction.transactionId,
                            transactionAccountId = accountFromDropdown.accountId!!,
                            transactionAccountTransferTo = accountToDropdown.accountId,
                            transactionAmount = binding.textInputEditAmount.text.toString()
                                .toDouble(),
                            transactionType = 3,
                            transactionTime = transaction.transactionTime
                        )
                        transactionViewModel.update(transaction)
                    }
                    hideKeyboard()
                    findNavController().navigateUp()
                }

            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(transactionId: Long) =
            TransferTransactionFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_TRANSACTION_ID, transactionId)
                }
            }
    }
}