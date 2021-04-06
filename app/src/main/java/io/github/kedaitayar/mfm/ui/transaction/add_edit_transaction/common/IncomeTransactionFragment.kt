package io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.databinding.FragmentIncomeTransactionBinding
import io.github.kedaitayar.mfm.data.entity.Transaction
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.AddEditTransactionViewModel
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.add_transaction.AddTransactionChild
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.add_transaction.AddTransactionFragment
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.edit_transaction.EditTransactionChild

private const val ARG_TRANSACTION = "io.github.kedaitayar.mfm.ui.transaction.IncomeTransactionFragment.TransactionId"

@AndroidEntryPoint
class IncomeTransactionFragment : Fragment(R.layout.fragment_income_transaction),
    AddTransactionChild, EditTransactionChild {
    private val addEditTransactionViewModel: AddEditTransactionViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private var _binding: FragmentIncomeTransactionBinding? = null
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
        _binding = FragmentIncomeTransactionBinding.bind(view)

        setupAccountDropdown()
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
            textInputEditAmount.addTextChangedListener {
                addEditTransactionViewModel.inputAmount = it.toString().toDoubleOrNull()
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
        addEditTransactionViewModel.allAccount.observe(viewLifecycleOwner, Observer { list ->
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
        addEditTransactionViewModel.onButtonAddClick(AddEditTransactionViewModel.TransactionType.INCOME)
//        val accountName = binding.autoCompleteAccount.text.toString()
//        val transactionAmount = binding.textInputEditAmount.text.toString()
//        if (accountName.isBlank()) {
//            if (parentFragment is AddTransactionFragment) {
//                (parentFragment as AddTransactionFragment).showSnackbar("Account cannot be empty")
//            }
//        } else if (transactionAmount.isBlank()) {
//            if (parentFragment is AddTransactionFragment) {
//                (parentFragment as AddTransactionFragment).showSnackbar("Amount cannot be empty")
//            }
//        } else {
//            val account = transactionViewModel.allAccount.value?.let { list ->
//                list.find { it.accountName == accountName }
//            }
//            if (account != null) {
//                CoroutineScope(Dispatchers.IO).launch {
//                    val transaction = Transaction(
//                        transactionAccountId = account.accountId!!,
//                        transactionAmount = binding.textInputEditAmount.text.toString()
//                            .toDouble(),
//                        transactionType = 2,
//                        transactionTime = OffsetDateTime.now()
//                    )
//                    val result = async { transactionViewModel.insert(transaction) }
//                    withContext(Dispatchers.Main) {
//                        if (result.await() > 0) {
//                            mainViewModel.setSnackbarText("Transaction added")
//                            hideKeyboard()
//                            findNavController().navigateUp()
//                        }
//                    }
//                }
//            }
//        }
    }

    override fun onButtonSaveClick() {
        addEditTransactionViewModel.onButtonSaveClick(AddEditTransactionViewModel.TransactionType.INCOME)
//        val accountName = binding.autoCompleteAccount.text.toString()
//        val transactionAmount = binding.textInputEditAmount.text.toString()
//        if (accountName.isBlank()) {
//            if (parentFragment is EditTransactionFragment) {
//                (parentFragment as EditTransactionFragment).showSnackbar("Account cannot be empty")
//            }
//        } else if (transactionAmount.isBlank()) {
//            if (parentFragment is EditTransactionFragment) {
//                (parentFragment as EditTransactionFragment).showSnackbar("Amount cannot be empty")
//            }
//        } else {
//            val account = transactionViewModel.allAccount.value?.let { list ->
//                list.find { it.accountName == accountName }
//            }
//            if (account != null) {
//                CoroutineScope(Dispatchers.IO).launch {
//                    val transaction = Transaction(
//                        transactionId = transaction.transactionId,
//                        transactionAccountId = account.accountId!!,
//                        transactionAmount = binding.textInputEditAmount.text.toString()
//                            .toDouble(),
//                        transactionType = 2,
//                        transactionTime = transaction.transactionTime
//                    )
//                    transactionViewModel.update(transaction)
//                }
//                hideKeyboard()
//                findNavController().navigateUp()
//            }
//        }
    }

    companion object {
        @JvmStatic
        fun newInstance(transaction: Transaction) =
            IncomeTransactionFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_TRANSACTION, transaction)
                }
            }
    }
}