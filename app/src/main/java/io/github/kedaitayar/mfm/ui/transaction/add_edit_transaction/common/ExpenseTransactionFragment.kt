package io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.common

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.entity.Budget
import io.github.kedaitayar.mfm.data.entity.Transaction
import io.github.kedaitayar.mfm.databinding.FragmentExpenseTransactionBinding
import io.github.kedaitayar.mfm.ui.main.MainViewModel
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.AddEditTransactionViewModel
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.add_transaction.AddTransactionChild
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.add_transaction.AddTransactionFragment
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.edit_transaction.EditTransactionChild
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.edit_transaction.EditTransactionFragment
import io.github.kedaitayar.mfm.util.SoftKeyboardManager.hideKeyboard
import io.github.kedaitayar.mfm.viewmodels.TransactionViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import java.time.OffsetDateTime


private const val ARG_TRANSACTION =
    "io.github.kedaitayar.mfm.ui.transaction.ExpenseTransactionFragment.Transaction"

@AndroidEntryPoint
class ExpenseTransactionFragment : Fragment(R.layout.fragment_expense_transaction),
    AddTransactionChild, EditTransactionChild {
    private val addEditTransactionViewModel: AddEditTransactionViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private var _binding: FragmentExpenseTransactionBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            addEditTransactionViewModel.transactionStateFlow.value =
//                it.getParcelable(ARG_TRANSACTION) ?: Transaction(transactionId = -1L)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentExpenseTransactionBinding.bind(view)

        setupAccountDropdown()
        setupBudgetDropdown()
        setupInputListener()

        if (addEditTransactionViewModel.transaction != null) {
            setupEditTransactionValue()
        }

//        addEditTransactionViewModel.transaction.observe(viewLifecycleOwner) {
//            it?.let {
//                if (it.transactionId != -1L) {
//                    setupEditTransactionValue()
//                }
//            }
//        }
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
//        val accountName = binding.autoCompleteAccount.text.toString()
//        val budgetName = binding.autoCompleteBudget.text.toString()
//        val transactionAmount = binding.textInputEditAmount.text.toString()
//        if (accountName.isBlank()) {
//            if (parentFragment is AddTransactionFragment) {
//                (parentFragment as AddTransactionFragment).showSnackbar("Account cannot be empty")
//            }
//        } else if (budgetName.isBlank()) {
//            if (parentFragment is AddTransactionFragment) {
//                (parentFragment as AddTransactionFragment).showSnackbar("Budget cannot be empty")
//            }
//        } else if (transactionAmount.isBlank()) {
//            if (parentFragment is AddTransactionFragment) {
//                (parentFragment as AddTransactionFragment).showSnackbar("Amount cannot be empty")
//            }
//        } else {
//            val account = transactionViewModel.allAccount.value?.let { list ->
//                list.find { it.accountName == accountName }
//            }
//            val budget = transactionViewModel.allBudget.value?.let { list ->
//                list.find { it.budgetName == budgetName }
//            }
//            if (account != null) {
//                if (budget != null) {
//                    CoroutineScope(Dispatchers.IO).launch {
//                        val transaction = Transaction(
//                            transactionAccountId = account.accountId!!,
//                            transactionBudgetId = budget.budgetId,
//                            transactionAmount = binding.textInputEditAmount.text.toString().toDouble(),
//                            transactionType = 1,
//                            transactionTime = OffsetDateTime.now()
//                        )
//                        val result = async { transactionViewModel.insert(transaction) }
//                        withContext(Dispatchers.Main) {
//                            if (result.await() > 0) {
//                                mainViewModel.setSnackbarText("Transaction added")
//                                hideKeyboard()
//                                findNavController().navigateUp()
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }

    override fun onButtonSaveClick() {
        addEditTransactionViewModel.onButtonSaveClick(AddEditTransactionViewModel.TransactionType.EXPENSE)
//        val accountName = binding.autoCompleteAccount.text.toString()
//        val budgetName = binding.autoCompleteBudget.text.toString()
//        val transactionAmount = binding.textInputEditAmount.text.toString()
//        if (accountName.isBlank()) {
//            if (parentFragment is EditTransactionFragment) {
//                (parentFragment as EditTransactionFragment).showSnackbar("Account cannot be empty")
//            }
//        } else if (budgetName.isBlank()) {
//            if (parentFragment is EditTransactionFragment) {
//                (parentFragment as EditTransactionFragment).showSnackbar("Budget cannot be empty")
//            }
//        } else if (transactionAmount.isBlank()) {
//            if (parentFragment is EditTransactionFragment) {
//                (parentFragment as EditTransactionFragment).showSnackbar("Amount cannot be empty")
//            }
//        } else {
//            val account = transactionViewModel.allAccount.value?.let { list ->
//                list.find { it.accountName == accountName }
//            }
//            val budget = transactionViewModel.allBudget.value?.let { list ->
//                list.find { it.budgetName == budgetName }
//            }
//            if (account != null) {
//                if (budget != null) {
//                    CoroutineScope(Dispatchers.IO).launch {
//                        val transaction = Transaction(
//                            transactionId = transaction.transactionId,
//                            transactionAccountId = account.accountId!!,
//                            transactionBudgetId = budget.budgetId,
//                            transactionAmount = binding.textInputEditAmount.text.toString().toDouble(),
//                            transactionType = 1,
//                            transactionTime = transaction.transactionTime
//                        )
//                        transactionViewModel.update(transaction)
//                    }
//                    hideKeyboard()
//                    findNavController().navigateUp()
//                }
//
//            }
//        }
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