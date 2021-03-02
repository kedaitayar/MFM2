package io.github.kedaitayar.mfm.ui.account

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
import io.github.kedaitayar.mfm.util.NavigationResult.setNavigationResult
import io.github.kedaitayar.mfm.util.SoftKeyboardManager.hideKeyboard
import io.github.kedaitayar.mfm.viewmodels.AccountViewModel
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.databinding.FragmentAddEditAccountBinding
import kotlinx.coroutines.*

private const val TAG = "AddEditAccountFragment"

@AndroidEntryPoint
class AddEditAccountFragment : Fragment(R.layout.fragment_add_edit_account) {
    private val accountViewModel: AccountViewModel by viewModels()
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
                    Log.i(
                        TAG,
                        "update account result: ${result.await()}"
                    )
                    withContext(Dispatchers.Main) {
                        setNavigationResult(result.await(), EDIT_ACCOUNT_RESULT_KEY)
                        findNavController().navigateUp()
                    }
                }
            } else {
                Log.i(TAG, "account name blank") //TODO: show popup account name cant be blank
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
                    Log.i(
                        TAG,
                        "add account result: ${result.await()}"
                    )
                    withContext(Dispatchers.Main) {
                        setNavigationResult(result.await(), ADD_ACCOUNT_RESULT_KEY)
                        findNavController().navigateUp()
                    }
                }

            } else {
                Log.i(TAG, "account name blank") //TODO: show popup account name cant be blank
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ADD_ACCOUNT_RESULT_KEY =
            "io.github.kedaitayar.mfm.ui.account.AddEditAccountFragment.add.result"
        const val EDIT_ACCOUNT_RESULT_KEY =
            "io.github.kedaitayar.mfm.ui.account.AddEditAccountFragment.edit.result"
    }
}