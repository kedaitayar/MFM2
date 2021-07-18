package io.github.kedaitayar.mfm.ui.dashboard.account.main_account

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ebner.roomdatabasebackup.core.RoomBackup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.MainActivity
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.database.MFMDatabase
import io.github.kedaitayar.mfm.databinding.FragmentAccountDashboardBinding
import io.github.kedaitayar.mfm.ui.main.MainFragmentDirections
import io.github.kedaitayar.mfm.ui.main.MainViewModel
import io.github.kedaitayar.mfm.util.exhaustive
import kotlinx.coroutines.flow.collect
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

@AndroidEntryPoint
class AccountDashboardFragment : Fragment(R.layout.fragment_account_dashboard) {
    private val accountDashboardViewModel: AccountDashboardViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentAccountDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAccountDashboardBinding.bind(view)

        childFragmentManager.beginTransaction().replace(R.id.account_list_fragment_container, AccountListFragment())
            .commit()

        setupSettingMenu()
        setupDashboardInfo()
        setupAddAccountButtonListener()
        setupEventListener()
    }

    private fun setupSettingMenu() {
        binding.buttonMore.setOnClickListener { buttonView ->
            val popupMenu = PopupMenu(requireContext(), buttonView)
            popupMenu.menuInflater.inflate(R.menu.menu_setting, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.backup -> {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Backup")
                            .setMessage(
                                "The backup file will be store in `/storage/emulated/0/Android/data/io.github.kedaitayar.mfm/files/backup/mfm_db-[DateTime].sqlite3`. " +
                                        "Move the file somewhere else because the file is deleted by Android after uninstalling the App." +
                                        " After the backup is done, the app will be restarted."
                            )
                            .setNegativeButton("Cancel") { dialog, _ ->
                                dialog.cancel()
                            }
                            .setPositiveButton("Backup") { _, _ ->
                                backup()
                            }
                            .show()
                        true
                    }
                    R.id.restore -> {
                        restore()
                        true
                    }
                    else -> true
                }
            }

            popupMenu.show()
        }
    }

    private fun backup() {
        RoomBackup()
            .context(requireContext())
            .database(MFMDatabase.getDatabase(requireContext()))
            .enableLogDebug(false)
            .backupIsEncrypted(false)
            .useExternalStorage(true)
            .apply {
                onCompleteListener { success, message ->
                    if (success) {
                        restartApp(Intent(requireContext(), MainActivity::class.java))
                    } else {
                        mainViewModel.showSnackbar("There an error. Error message: $message", Snackbar.LENGTH_SHORT)
                    }
                }
            }
            .backup()
    }

    private fun restore() {
        RoomBackup()
            .context(requireContext())
            .database(MFMDatabase.getDatabase(requireContext()))
            .enableLogDebug(false)
            .backupIsEncrypted(false)
            .useExternalStorage(true)
            .apply {
                onCompleteListener { success, message ->
                    if (success) {
                        restartApp(Intent(requireContext(), MainActivity::class.java))
                    } else {
                        mainViewModel.showSnackbar("There an error. Error message: $message", Snackbar.LENGTH_SHORT)
                    }
                }
            }
            .restore()
    }

    private fun setupEventListener() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            accountDashboardViewModel.accountDashboardEvent.collect { event ->
                when (event) {
                    AccountDashboardViewModel.AccountDashboardEvent.NavigateToAddAccount -> {
                        val action = MainFragmentDirections.actionMainFragmentToAddEditAccountFragment()
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }
    }

    private fun setupDashboardInfo() {
        val resources = requireContext().resources
        val format = DecimalFormatSymbols().apply {
            groupingSeparator = ' '
        }
        val formatter = DecimalFormat(resources.getString(R.string.currency_format)).apply {
            decimalFormatSymbols = format
        }
        accountDashboardViewModel.thisMonthSpending.observe(viewLifecycleOwner) {
            it?.let {
                binding.textViewSpendingThisMonthAmount.text =
                    resources.getString(R.string.currency_symbol, formatter.format(it))
            }
        }
        accountDashboardViewModel.nextMonthBudgeted.observe(viewLifecycleOwner) {
            it?.let {
                binding.textViewBudgetedNextMonthAmount.text =
                    resources.getString(R.string.currency_symbol, formatter.format(it))
            }
        }
        accountDashboardViewModel.totalBudgetedAndGoal.observe(viewLifecycleOwner) {
            it?.let {
                binding.textViewUncompletedBudget.text = resources.getString(
                    R.string.currency_symbol,
                    formatter.format(it.budgetGoal - it.budgetTransactionAmount)
                )
                binding.ringView.setRingProgress((it.budgetTransactionAmount / it.budgetGoal * 100).toInt())
            }
        }
        accountDashboardViewModel.notBudgetedAmount.observe(viewLifecycleOwner) {
            it?.let {
                binding.textViewNotBudgetedAmount.text =
                    resources.getString(R.string.currency_symbol, formatter.format(it))
            }
        }
    }

    private fun setupAddAccountButtonListener() {
        binding.buttonAddAccount.setOnClickListener {
            accountDashboardViewModel.onAddNewAccountClick()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}