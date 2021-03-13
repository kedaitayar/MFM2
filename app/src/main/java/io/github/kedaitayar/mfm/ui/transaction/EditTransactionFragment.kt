package io.github.kedaitayar.mfm.ui.transaction

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentEditTransactionBinding
import io.github.kedaitayar.mfm.util.SoftKeyboardManager.hideKeyboard
import io.github.kedaitayar.mfm.viewmodels.TransactionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "EditTransactionFragment"

@AndroidEntryPoint
class EditTransactionFragment : Fragment(R.layout.fragment_edit_transaction) {
    private val transactionViewModel: TransactionViewModel by viewModels()
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
                if (fragment is AddTransactionChild) {
                    (fragment as AddTransactionChild).onButtonAddClick()
                }
            }
        }
    }

    private fun setupToolbar() {
//        binding.topAppBar.inflateMenu(R.menu.menu_save)
        binding.topAppBar.setNavigationIcon(R.drawable.ic_baseline_close_24)
        binding.topAppBar.setNavigationOnClickListener {
            hideKeyboard()
            findNavController().navigateUp()
        }
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.save -> {

                    hideKeyboard()
                    findNavController().navigateUp()
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}