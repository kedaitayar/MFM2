package io.github.kedaitayar.mfm.ui.transaction

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.entity.Transaction
import io.github.kedaitayar.mfm.databinding.FragmentEditTransactionBinding
import io.github.kedaitayar.mfm.ui.account.MainAccountFragment
import io.github.kedaitayar.mfm.util.SoftKeyboardManager.hideKeyboard
import io.github.kedaitayar.mfm.viewmodels.SharedViewModel
import io.github.kedaitayar.mfm.viewmodels.TransactionViewModel
import kotlinx.coroutines.*

private const val TAG = "EditTransactionFragment"

@AndroidEntryPoint
class EditTransactionFragment : Fragment(R.layout.fragment_edit_transaction) {
    private val transactionViewModel: TransactionViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentEditTransactionBinding? = null
    private val binding get() = _binding!!
    private val args: EditTransactionFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditTransactionBinding.inflate(inflater, container, false)
        context ?: return binding.root
        setupToolbar()
        setupFragment()

        return binding.root
    }

    private fun setupFragment() {
        CoroutineScope(Dispatchers.IO).launch {
            val transaction = transactionViewModel.getTransactionById(args.transactionId)
            Log.i(TAG, "setupFragment: $transaction")
            val fragment = when (transaction.transactionType) {
                1 -> {
                    ExpenseTransactionFragment.newInstance(args.transactionId)
                }
                2 -> {
                    IncomeTransactionFragment.newInstance(args.transactionId)
                }
                3 -> {
                    TransferTransactionFragment.newInstance(args.transactionId)
                }
                else -> {
                    Fragment()
                }
            }
            childFragmentManager.beginTransaction().replace(R.id.fragment_container_edit_transaction, fragment).commit()
            binding.buttonSave.setOnClickListener {
                if (fragment is EditTransactionChild) {
                    (fragment as EditTransactionChild).onButtonSaveClick(transaction)
                }
            }
        }
    }

    private fun setupToolbar() {
        binding.topAppBar.inflateMenu(R.menu.menu_delete)
        binding.topAppBar.setNavigationIcon(R.drawable.ic_baseline_close_24)
        binding.topAppBar.setNavigationOnClickListener {
            hideKeyboard()
            findNavController().navigateUp()
        }
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("Delete transaction?")
                        .setNegativeButton("Cancel") { dialog, which -> }
                        .setPositiveButton("Delete") { dialog, which ->
                            CoroutineScope(Dispatchers.IO).launch {
                                val transaction = Transaction(transactionId = args.transactionId)
                                val result = async { transactionViewModel.delete(transaction) }
                                withContext(Dispatchers.Main) {
                                    if (result.await() > 0) {
                                        sharedViewModel.setSnackbarText("Transaction deleted")
                                        findNavController().navigateUp()
                                    }
                                }
                            }
                        }.show()
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    fun showSnackbar(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT)
            .setAnchorView(binding.buttonSave)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}