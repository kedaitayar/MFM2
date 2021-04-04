package io.github.kedaitayar.mfm.ui.dashboard.account.add_edit_account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.util.SoftKeyboardManager.hideKeyboard
import io.github.kedaitayar.mfm.databinding.FragmentAddEditAccountBinding
import io.github.kedaitayar.mfm.util.exhaustive
import io.github.kedaitayar.mfm.ui.main.MainViewModel
import kotlinx.coroutines.flow.collect

private const val TAG = "AddEditAccountFragment"

@AndroidEntryPoint
class AddEditAccountFragment : Fragment(R.layout.fragment_add_edit_account) {
    private val addEditAccountViewModel: AddEditAccountViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentAddEditAccountBinding? = null
    private val binding get() = _binding!!
    private val args: AddEditAccountFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddEditAccountBinding.bind(view)

        binding.topAppBar.setNavigationIcon(R.drawable.ic_baseline_close_24)
        binding.topAppBar.setNavigationOnClickListener {
            hideKeyboard()
            findNavController().navigateUp()
        }

        if (args.account != null) {
            editAccountSetup()
        } else {
            addAccountSetup()
        }
        setupToolbar()
        setupEventListener()
    }

    private fun setupEventListener() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            addEditAccountViewModel.addEditAccountEvent.collect { event ->
                when (event) {
                    is AddEditAccountViewModel.AddEditAccountEvent.NavigateBackWithAddResult -> {
                        if (event.result == 1L) {
                            mainViewModel.showSnackbar("Account added", Snackbar.LENGTH_SHORT)
                        } else {
                            mainViewModel.showSnackbar("Account add failed", Snackbar.LENGTH_SHORT)
                        }
                        hideKeyboard()
                        findNavController().navigateUp()
                    }
                    is AddEditAccountViewModel.AddEditAccountEvent.NavigateBackWithDeleteResult -> {
                        if (event.result == 1) {
                            mainViewModel.showSnackbar("Account deleted", Snackbar.LENGTH_SHORT)
                        } else {
                            mainViewModel.showSnackbar("Account delete failed", Snackbar.LENGTH_SHORT)
                        }
                        hideKeyboard()
                        findNavController().navigateUp()
                    }
                    is AddEditAccountViewModel.AddEditAccountEvent.NavigateBackWithEditResult -> {
                        if (event.result == 1) {
                            mainViewModel.showSnackbar("Account updated", Snackbar.LENGTH_SHORT)
                        } else {
                            mainViewModel.showSnackbar("Account update failed", Snackbar.LENGTH_SHORT)
                        }
                        hideKeyboard()
                        findNavController().navigateUp()
                    }
                    is AddEditAccountViewModel.AddEditAccountEvent.ShowSnackbar -> {
                        Snackbar.make(requireView(), event.msg, event.length)
                            .setAnchorView(binding.buttonAddAccount)
                            .show()
                    }
                }.exhaustive
            }
        }
    }

    private fun editAccountSetup() {
        binding.apply {
            buttonAddAccount.text = "Save"
            topAppBar.title = "Edit Account"
            textInputEditAccountName.setText(args.account!!.accountName)
            textInputEditAccountName.addTextChangedListener { editable ->
                addEditAccountViewModel.account?.accountName = editable.toString()
            }
            buttonAddAccount.setOnClickListener {
                addEditAccountViewModel.onSaveClick()
            }
        }
    }

    private fun addAccountSetup() {
        binding.apply {
            topAppBar.title = "Add Account"
            textInputEditAccountName.addTextChangedListener { editable ->
                addEditAccountViewModel.account?.accountName = editable.toString()
            }
            buttonAddAccount.setOnClickListener {
                addEditAccountViewModel.onAddClick()
            }
        }
    }

    private fun setupToolbar() {
        binding.topAppBar.inflateMenu(R.menu.menu_delete)
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("Delete account?")
                        .setNegativeButton("Cancel") { dialog, which -> }
                        .setPositiveButton("Delete") { dialog, which ->
                            addEditAccountViewModel.onDeleteClick()
                        }.show()
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