package io.github.kedaitayar.mfm.ui.transaction

import android.os.Bundle
import android.text.Editable
import android.util.Log
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
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.entity.Budget
import io.github.kedaitayar.mfm.databinding.FragmentExpenseTransactionBinding
import io.github.kedaitayar.mfm.util.SoftKeyboardManager.hideKeyboard
import io.github.kedaitayar.mfm.viewmodels.TransactionViewModel
import io.github.kedaitayar.mfm.data.entity.Transaction
import io.github.kedaitayar.mfm.viewmodels.SharedViewModel
import kotlinx.coroutines.*
import java.time.OffsetDateTime

private const val ARG_TRANSACTION_ID =
    "io.github.kedaitayar.mfm.ui.transaction.ExpenseTransactionFragment.TransactionId"

@AndroidEntryPoint
class ExpenseTransactionFragment : Fragment(R.layout.fragment_expense_transaction),
    AddTransactionChild, EditTransactionChild {
    private val transactionViewModel: TransactionViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentExpenseTransactionBinding? = null
    private val binding get() = _binding!!
    private var transactionId: Long = -1L

    private var accountList = listOf<Account>()
    private var budgetList = listOf<Budget>()

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
        _binding = FragmentExpenseTransactionBinding.inflate(inflater, container, false)
        context ?: return binding.root

        setupAccountDropdown()
        setupBudgetDropdown()

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
                    list?.let {
                        binding.autoCompleteAccount.setText(list.firstOrNull { it.accountId == transaction.transactionAccountId }?.accountName, false)
                    }
                })
                transactionViewModel.allBudget.observe(viewLifecycleOwner, Observer { list ->
                    list?.let {
                        binding.autoCompleteBudget.setText(list.firstOrNull { it.budgetId == transaction.transactionBudgetId }?.budgetName, false)
                    }
                })
                binding.textInputEditAmount.setText(transaction.transactionAmount.toString())
            }
        }
    }

    private fun setupAccountDropdown() {
        transactionViewModel.allAccount.observe(viewLifecycleOwner, Observer { list ->
            list?.let { list2 ->
                accountList = list2
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

    private fun setupBudgetDropdown() {
        transactionViewModel.allBudget.observe(viewLifecycleOwner, Observer { list ->
            list?.let { list2 ->
                budgetList = list2
                val items: List<String> = list2.map { it.budgetName }
                val adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    items
                )
                binding.autoCompleteBudget.setAdapter(adapter)
            }
        })
    }

    override fun onButtonAddClick() {
        val accountName = binding.autoCompleteAccount.text.toString()
        val budgetName = binding.autoCompleteBudget.text.toString()
        val transactionAmount = binding.textInputEditAmount.text.toString()
        if (accountName.isBlank()) {
            if (parentFragment is AddTransactionFragment) {
                (parentFragment as AddTransactionFragment).showSnackbar("Account cannot be empty")
            }
        } else if (budgetName.isBlank()) {
            if (parentFragment is AddTransactionFragment) {
                (parentFragment as AddTransactionFragment).showSnackbar("Budget cannot be empty")
            }
        } else if (transactionAmount.isBlank()) {
            if (parentFragment is AddTransactionFragment) {
                (parentFragment as AddTransactionFragment).showSnackbar("Amount cannot be empty")
            }
        } else {
            val account = transactionViewModel.allAccount.value?.let { list ->
                list.find { it.accountName == accountName }
            }
            val budget = transactionViewModel.allBudget.value?.let { list ->
                list.find { it.budgetName == budgetName }
            }
            if (account != null) {
                if (budget != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val transaction = Transaction(
                            transactionAccountId = account.accountId!!,
                            transactionBudgetId = budget.budgetId,
                            transactionAmount = binding.textInputEditAmount.text.toString().toDouble(),
                            transactionType = 1,
                            transactionTime = OffsetDateTime.now()
                        )
                        val result = async { transactionViewModel.insert(transaction) }
                        withContext(Dispatchers.Main) {
                            if (result.await() > 0) {
                                sharedViewModel.setSnackbarText("Transaction added")
                                hideKeyboard()
                                findNavController().navigateUp()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onButtonSaveClick(transaction: Transaction) {
        val accountName = binding.autoCompleteAccount.text.toString()
        val budgetName = binding.autoCompleteBudget.text.toString()
        val transactionAmount = binding.textInputEditAmount.text.toString()
        if (accountName.isBlank()) {
            if (parentFragment is EditTransactionFragment) {
                (parentFragment as EditTransactionFragment).showSnackbar("Account cannot be empty")
            }
        } else if (budgetName.isBlank()) {
            if (parentFragment is EditTransactionFragment) {
                (parentFragment as EditTransactionFragment).showSnackbar("Budget cannot be empty")
            }
        } else if (transactionAmount.isBlank()) {
            if (parentFragment is EditTransactionFragment) {
                (parentFragment as EditTransactionFragment).showSnackbar("Amount cannot be empty")
            }
        } else {
            val account = transactionViewModel.allAccount.value?.let { list ->
                list.find { it.accountName == accountName }
            }
            val budget = transactionViewModel.allBudget.value?.let { list ->
                list.find { it.budgetName == budgetName }
            }
            if (account != null) {
                if (budget != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val transaction = Transaction(
                            transactionId = transaction.transactionId,
                            transactionAccountId = account.accountId!!,
                            transactionBudgetId = budget.budgetId,
                            transactionAmount = binding.textInputEditAmount.text.toString().toDouble(),
                            transactionType = 1,
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
        fun newInstance(transactionId: Long) =
            ExpenseTransactionFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_TRANSACTION_ID, transactionId)
                }
            }
    }
}