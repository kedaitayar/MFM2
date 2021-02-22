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
import io.github.kedaitayar.mfm.databinding.FragmentExpenseTransactionBinding
import io.github.kedaitayar.mfm.util.SoftKeyboardManager.hideKeyboard
import io.github.kedaitayar.mfm.viewmodels.TransactionViewModel
import io.github.kedaitayar.mfm2.data.entity.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

private const val TAG = "ExpenseTransactionFragm"

@AndroidEntryPoint
class ExpenseTransactionFragment : Fragment(R.layout.fragment_expense_transaction),
    AddTransactionChild {
    private val transactionViewModel: TransactionViewModel by viewModels()
    private var _binding: FragmentExpenseTransactionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExpenseTransactionBinding.inflate(inflater, container, false)
        context ?: return binding.root

        setupAccountDropdown()
        setupBudgetDropdown()

        return binding.root
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

    private fun setupBudgetDropdown() {
        transactionViewModel.allBudget.observe(viewLifecycleOwner, Observer { list ->
            list?.let { list2 ->
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

    override fun onButtonSaveClick() {
        Log.i(TAG, "onButtonSaveClick: Expense")
        val accountName = binding.autoCompleteAccount.text.toString()
        val budgetName = binding.autoCompleteBudget.text.toString()
        val transactionAmount = binding.textInputEditAmount.text.toString()
        if (accountName.isNullOrBlank() || budgetName.isNullOrBlank() || transactionAmount.isNullOrBlank()) {
            // TODO: notify user input validation error
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
                            transactionAmount = binding.textInputEditAmount.text.toString()
                                .toDouble(),
                            transactionType = 1,
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

    override fun onResume() {
        super.onResume()
        (parentFragment as AddTransactionFragment).setCurrentPage(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}