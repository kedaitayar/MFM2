package io.github.kedaitayar.mfm.ui.dashboard.account.main_account

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
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
import io.github.kedaitayar.mfm.util.notNull
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

private const val TAG = "AccountDashboardFragmen"

@AndroidEntryPoint
class AccountDashboardFragment : Fragment(R.layout.fragment_account_dashboard) {
    private val accountDashboardViewModel: AccountDashboardViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val binding: FragmentAccountDashboardBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
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
    }

    private fun setupDashboardInfo() {
        val resources = requireContext().resources
        val format = DecimalFormatSymbols().apply {
            groupingSeparator = ' '
        }
        val formatter = DecimalFormat(resources.getString(R.string.currency_format)).apply {
            decimalFormatSymbols = format
        }
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    accountDashboardViewModel.thisMonthSpending.collect {
                        binding.textViewSpendingThisMonthAmount.text =
                            resources.getString(R.string.currency_symbol, formatter.format(it.notNull()))
                    }
                }
                launch {
                    accountDashboardViewModel.totalBudgetedAndGoal.collect {
                        binding.textViewUncompletedBudget.text = resources.getString(
                            R.string.currency_symbol,
                            formatter.format(it.uncompletedGoal.notNull())
                        )
                        binding.ringView.setRingProgress(((it.budgetGoal.notNull() - it.uncompletedGoal.notNull()) / it.budgetGoal.notNull() * 100).toInt())
                    }
                }
                launch {
                    accountDashboardViewModel.nextMonthBudgeted.collect {
                        binding.textViewBudgetedNextMonthAmount.text =
                            resources.getString(R.string.currency_symbol, formatter.format(it.notNull()))
                    }
                }
                launch {
                    accountDashboardViewModel.notBudgetedAmount.collect {
                        binding.textViewNotBudgetedAmount.text =
                            resources.getString(R.string.currency_symbol, formatter.format(it))
                    }
                }
            }
        }
    }

    private fun setupAddAccountButtonListener() {
        binding.buttonAddAccount.setOnClickListener {
            accountDashboardViewModel.onAddNewAccountClick()
        }
    }
}