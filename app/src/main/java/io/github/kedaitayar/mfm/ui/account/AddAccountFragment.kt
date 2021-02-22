package io.github.kedaitayar.mfm.ui.account

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentAddAccountBinding
import io.github.kedaitayar.mfm.util.NavigationResult.setNavigationResult
import io.github.kedaitayar.mfm.util.SoftKeyboardManager.hideKeyboard
import io.github.kedaitayar.mfm.viewmodels.AccountViewModel
import io.github.kedaitayar.mfm2.data.entity.Account
import kotlinx.coroutines.*

private const val TAG = "AddAccountFragment"

@AndroidEntryPoint
class AddAccountFragment : Fragment(R.layout.fragment_add_account) {
    private val accountViewModel: AccountViewModel by viewModels()
    private var _binding: FragmentAddAccountBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddAccountBinding.inflate(inflater, container, false)
        context ?: return binding.root

        addAccountButtonSetup()

        return binding.root
    }

    private fun addAccountButtonSetup() {
        binding.buttonAddAccount.setOnClickListener {
            if (!binding.textInputEditAccountName.text.isNullOrBlank()) {
                hideKeyboard()
                val account = Account(accountName = binding.textInputEditAccountName.text.toString())
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
            "io.github.kedaitayar.mfm.ui.account.AddAccountFragment.result"
    }
}