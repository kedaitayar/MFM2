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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.entity.Budget
import io.github.kedaitayar.mfm.databinding.FragmentTransferTransactionBinding
import io.github.kedaitayar.mfm.util.SoftKeyboardManager.hideKeyboard
import io.github.kedaitayar.mfm.viewmodels.TransactionViewModel
import io.github.kedaitayar.mfm.data.entity.Transaction
import io.github.kedaitayar.mfm.databinding.FragmentExpenseTransactionBinding
import io.github.kedaitayar.mfm.ui.main.MainViewModel
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.AddEditTransactionViewModel
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.add_transaction.AddTransactionChild
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.add_transaction.AddTransactionFragment
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.edit_transaction.EditTransactionChild
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.edit_transaction.EditTransactionFragment
import kotlinx.coroutines.*
import java.time.OffsetDateTime

private const val ARG_TRANSACTION =
    "io.github.kedaitayar.mfm.ui.transaction.TransferTransactionFragment.TransactionId"

@AndroidEntryPoint
class TransferTransactionFragment : Fragment(R.layout.fragment_transfer_transaction), AddTransactionChild,
    EditTransactionChild {
    private val addEditTransactionViewModel: AddEditTransactionViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private var _binding: FragmentTransferTransactionBinding? = null
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
        _binding = FragmentTransferTransactionBinding.bind(view)

        setupAccountFromDropdown()
        setupAccountToDropdown()
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
            autoCompleteTransferFrom.setOnItemClickListener { parent, view, position, id ->
                addEditTransactionViewModel.inputAccountFrom = parent.getItemAtPosition(position) as Account?
            }
            autoCompleteTransferTo.setOnItemClickListener { parent, view, position, id ->
                addEditTransactionViewModel.inputAccountTo = parent.getItemAtPosition(position) as Account?
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
//        addEditTransactionViewModel.transaction.observe(viewLifecycleOwner) {
//            it?.let {
//                addEditTransactionViewModel.inputAmount = it.transactionAmount
//                binding.textInputEditAmount.setText(it.transactionAmount.toString())
//            }
//        }
    }

    private fun setupAccountFromDropdown() {
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
                binding.autoCompleteTransferFrom.setAdapter(adapter)
            }
        }
    }

    private fun setupAccountToDropdown() {
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
                binding.autoCompleteTransferTo.setAdapter(adapter)
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

    override fun onButtonAddClick() {
        addEditTransactionViewModel.onButtonAddClick(AddEditTransactionViewModel.TransactionType.TRANSFER)
//        val accountFromDropdown = binding.autoCompleteTransferFrom.text.toString()
//        val accountToDropdown = binding.autoCompleteTransferTo.text.toString()
//        val transactionAmount = binding.textInputEditAmount.text.toString()
//        if (accountFromDropdown.isBlank()) {
//            if (parentFragment is AddTransactionFragment) {
//                (parentFragment as AddTransactionFragment).showSnackbar("Account cannot be empty")
//            }
//        } else if (accountToDropdown.isBlank()) {
//            if (parentFragment is AddTransactionFragment) {
//                (parentFragment as AddTransactionFragment).showSnackbar("Budget cannot be empty")
//            }
//        } else if (transactionAmount.isBlank()) {
//            if (parentFragment is AddTransactionFragment) {
//                (parentFragment as AddTransactionFragment).showSnackbar("Amount cannot be empty")
//            }
//        } else {
//            val accountFromDropdown = transactionViewModel.allAccount.value?.let { list ->
//                list.find { it.accountName == accountFromDropdown }
//            }
//            val accountToDropdown = transactionViewModel.allAccount.value?.let { list ->
//                list.find { it.accountName == accountToDropdown }
//            }
//            if (accountFromDropdown != null) {
//                if (accountToDropdown != null) {
//                    CoroutineScope(Dispatchers.IO).launch {
//                        val transaction = Transaction(
//                            transactionAccountId = accountFromDropdown.accountId!!,
//                            transactionAccountTransferTo = accountToDropdown.accountId,
//                            transactionAmount = binding.textInputEditAmount.text.toString()
//                                .toDouble(),
//                            transactionType = 3,
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
//
//            }
//        }
    }

    override fun onButtonSaveClick() {
        addEditTransactionViewModel.onButtonSaveClick(AddEditTransactionViewModel.TransactionType.TRANSFER)
//        val accountFromDropdown = binding.autoCompleteTransferFrom.text.toString()
//        val accountToDropdown = binding.autoCompleteTransferTo.text.toString()
//        val transactionAmount = binding.textInputEditAmount.text.toString()
//        if (accountFromDropdown.isBlank()) {
//            if (parentFragment is EditTransactionFragment) {
//                (parentFragment as EditTransactionFragment).showSnackbar("Account cannot be empty")
//            }
//        } else if (accountToDropdown.isBlank()) {
//            if (parentFragment is EditTransactionFragment) {
//                (parentFragment as EditTransactionFragment).showSnackbar("Budget cannot be empty")
//            }
//        } else if (transactionAmount.isBlank()) {
//            if (parentFragment is EditTransactionFragment) {
//                (parentFragment as EditTransactionFragment).showSnackbar("Amount cannot be empty")
//            }
//        } else {
//            val accountFromDropdown = transactionViewModel.allAccount.value?.let { list ->
//                list.find { it.accountName == accountFromDropdown }
//            }
//            val accountToDropdown = transactionViewModel.allAccount.value?.let { list ->
//                list.find { it.accountName == accountToDropdown }
//            }
//            if (accountFromDropdown != null) {
//                if (accountToDropdown != null) {
//                    CoroutineScope(Dispatchers.IO).launch {
//                        val transaction = Transaction(
//                            transactionId = transaction.transactionId,
//                            transactionAccountId = accountFromDropdown.accountId!!,
//                            transactionAccountTransferTo = accountToDropdown.accountId,
//                            transactionAmount = binding.textInputEditAmount.text.toString()
//                                .toDouble(),
//                            transactionType = 3,
//                            transactionTime = transaction.transactionTime
//                        )
//                        transactionViewModel.update(transaction)
//                    }
//                    hideKeyboard()
//                    findNavController().navigateUp()
//                }
//            }
//        }
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