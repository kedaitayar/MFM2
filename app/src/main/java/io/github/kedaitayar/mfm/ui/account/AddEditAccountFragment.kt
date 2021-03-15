package io.github.kedaitayar.mfm.ui.account

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
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.util.NavigationResult.setNavigationResult
import io.github.kedaitayar.mfm.util.SoftKeyboardManager.hideKeyboard
import io.github.kedaitayar.mfm.viewmodels.AccountViewModel
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.databinding.FragmentAddEditAccountBinding
import io.github.kedaitayar.mfm.viewmodels.SharedViewModel
import kotlinx.coroutines.*

private const val TAG = "AddEditAccountFragment"

@AndroidEntryPoint
class AddEditAccountFragment : Fragment(R.layout.fragment_add_edit_account) {
    private val accountViewModel: AccountViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentAddEditAccountBinding? = null
    private val binding get() = _binding!!
    private val args: AddEditAccountFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddEditAccountBinding.inflate(inflater, container, false)
        context ?: return binding.root

        if (args.accountId == -1L) {
            addAccountButtonSetup()
        } else {
            editAccountSetup()
        }

        return binding.root
    }

    private fun editAccountSetup() {
        binding.buttonAddAccount.text = "Save"
        CoroutineScope(Dispatchers.IO).launch {
            val account = accountViewModel.getAccountById(args.accountId)
            withContext(Dispatchers.Main) {
                binding.textInputEditAccountName.setText(account.accountName)
            }
        }
        binding.buttonAddAccount.setOnClickListener {
            if (!binding.textInputEditAccountName.text.isNullOrBlank()) {
                hideKeyboard()
                val account =
                    Account(accountId = args.accountId, accountName = binding.textInputEditAccountName.text.toString())
                CoroutineScope(Dispatchers.IO).launch {
                    val result = async { accountViewModel.update(account) }
                    withContext(Dispatchers.Main) {
                        if (result.await() > 0) {
                            sharedViewModel.setSnackbarText("Account updated")
                        }
                        findNavController().navigateUp()
                    }
                }
            } else {
                Snackbar.make(binding.root, "Account name cannot be blank", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun addAccountButtonSetup() {
        binding.buttonAddAccount.setOnClickListener {
            if (!binding.textInputEditAccountName.text.isNullOrBlank()) {
                hideKeyboard()
                val account =
                    Account(accountName = binding.textInputEditAccountName.text.toString())
                CoroutineScope(Dispatchers.IO).launch {
                    val result = async { accountViewModel.insert(account) }
                    withContext(Dispatchers.Main) {
                        if (result.await() > 0) {
                            sharedViewModel.setSnackbarText("Account added")
                        }
                        findNavController().navigateUp()
                    }
                }

            } else {
                Snackbar.make(binding.root, "Account name cannot be blank", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}