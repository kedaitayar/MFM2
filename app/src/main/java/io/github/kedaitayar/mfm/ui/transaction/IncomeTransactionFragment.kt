package io.github.kedaitayar.mfm.ui.transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentIncomeTransactionBinding
import io.github.kedaitayar.mfm.util.SoftKeyboardManager.hideKeyboard
import io.github.kedaitayar.mfm.viewmodels.TransactionViewModel
import io.github.kedaitayar.mfm.data.entity.Transaction
import io.github.kedaitayar.mfm.ui.main.MainViewModel
import kotlinx.coroutines.*
import java.time.OffsetDateTime

private const val ARG_TRANSACTION_ID = "io.github.kedaitayar.mfm.ui.transaction.IncomeTransactionFragment.TransactionId"

@AndroidEntryPoint
class IncomeTransactionFragment : Fragment(R.layout.fragment_income_transaction),
    AddTransactionChild, EditTransactionChild {
    private val transactionViewModel: TransactionViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentIncomeTransactionBinding? = null
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
        _binding = FragmentIncomeTransactionBinding.inflate(inflater, container, false)
        context ?: return binding.root

        setupAccountDropdown()
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
                    binding.autoCompleteAccount.setText(list.firstOrNull { it.accountId == transaction.transactionAccountId }?.accountName, false)
                })
                binding.textInputEditAmount.setText(transaction.transactionAmount.toString())
            }
        }
    }

    private fun setupAccountDropdown() {
        transactionViewModel.allAccount.observe(viewLifecycleOwner, Observer { list ->
            list?.let { list2 ->
                val items: List<String> = list2.map { it.accountName }
                val adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    items
                )
                binding.autoCompleteAccount.setAdapter(adapter)
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
        val accountName = binding.autoCompleteAccount.text.toString()
        val transactionAmount = binding.textInputEditAmount.text.toString()
        if (accountName.isBlank()) {
            if (parentFragment is AddTransactionFragment) {
                (parentFragment as AddTransactionFragment).showSnackbar("Account cannot be empty")
            }
        } else if (transactionAmount.isBlank()) {
            if (parentFragment is AddTransactionFragment) {
                (parentFragment as AddTransactionFragment).showSnackbar("Amount cannot be empty")
            }
        } else {
            val account = transactionViewModel.allAccount.value?.let { list ->
                list.find { it.accountName == accountName }
            }
            if (account != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    val transaction = Transaction(
                        transactionAccountId = account.accountId!!,
                        transactionAmount = binding.textInputEditAmount.text.toString()
                            .toDouble(),
                        transactionType = 2,
                        transactionTime = OffsetDateTime.now()
                    )
                    val result = async { transactionViewModel.insert(transaction) }
                    withContext(Dispatchers.Main) {
                        if (result.await() > 0) {
                            mainViewModel.setSnackbarText("Transaction added")
                            hideKeyboard()
                            findNavController().navigateUp()
                        }
                    }
                }
            }
        }
    }

    override fun onButtonSaveClick(transaction: Transaction) {
        val accountName = binding.autoCompleteAccount.text.toString()
        val transactionAmount = binding.textInputEditAmount.text.toString()
        if (accountName.isBlank()) {
            if (parentFragment is EditTransactionFragment) {
                (parentFragment as EditTransactionFragment).showSnackbar("Account cannot be empty")
            }
        } else if (transactionAmount.isBlank()) {
            if (parentFragment is EditTransactionFragment) {
                (parentFragment as EditTransactionFragment).showSnackbar("Amount cannot be empty")
            }
        } else {
            val account = transactionViewModel.allAccount.value?.let { list ->
                list.find { it.accountName == accountName }
            }
            if (account != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    val transaction = Transaction(
                        transactionId = transaction.transactionId,
                        transactionAccountId = account.accountId!!,
                        transactionAmount = binding.textInputEditAmount.text.toString()
                            .toDouble(),
                        transactionType = 2,
                        transactionTime = transaction.transactionTime
                    )
                    transactionViewModel.update(transaction)
                }
                hideKeyboard()
                findNavController().navigateUp()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(transactionId: Long) =
            IncomeTransactionFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_TRANSACTION_ID, transactionId)
                }
            }
    }
}