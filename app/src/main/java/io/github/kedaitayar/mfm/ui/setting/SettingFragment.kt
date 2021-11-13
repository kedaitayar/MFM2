package io.github.kedaitayar.mfm.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.ebner.roomdatabasebackup.core.RoomBackup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import io.github.kedaitayar.mfm.MainActivity
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.database.MFMDatabase
import io.github.kedaitayar.mfm.databinding.FragmentSettingBinding
import io.github.kedaitayar.mfm.ui.main.MainFragmentDirections
import io.github.kedaitayar.mfm.ui.main.MainViewModel
import io.github.kedaitayar.mfm.util.SoftKeyboardManager.hideKeyboard
import timber.log.Timber

class SettingFragment : Fragment(R.layout.fragment_setting) {
    val binding: FragmentSettingBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTopbar()
        backupSetup()
        restoreSetup()
        setupQuickTransaction()
    }

    private fun setupTopbar() {
        binding.topAppBar.apply {
            setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
            setNavigationOnClickListener {
                hideKeyboard()
                requireActivity().onBackPressed()
            }
            title = "Setting"
        }
    }

    private fun setupQuickTransaction() {
        binding.quickTransaction.setOnClickListener {
            val action = SettingFragmentDirections.actionSettingFragmentToQuickTransactionMainFragment()
            findNavController().navigate(action)
        }
    }

    private fun restoreSetup() {
        binding.restore.setOnClickListener {
            restore()
        }
    }

    private fun backupSetup() {
        binding.backup.setOnClickListener {
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
                        showSnackBar("There an error. Error message: $message", Snackbar.LENGTH_SHORT)
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
                        showSnackBar("There an error. Error message: $message", Snackbar.LENGTH_SHORT)
                    }
                }
            }
            .restore()
    }

    private fun showSnackBar(text: String, length: Int) {
        Snackbar.make(binding.root, text, length).show()
    }
}