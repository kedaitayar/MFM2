package io.github.kedaitayar.mfm.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import de.raphaelebner.roomdatabasebackup.core.RoomBackup
import io.github.kedaitayar.mfm.MainActivity
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.database.MFMDatabase
import io.github.kedaitayar.mfm.databinding.FragmentSettingBinding
import io.github.kedaitayar.mfm.util.SoftKeyboardManager.hideKeyboard

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
            val action =
                SettingFragmentDirections.actionSettingFragmentToQuickTransactionMainFragment()
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
            backup()
        }
    }

    private fun backup() {
        (activity as MainActivity).backup
            .database(MFMDatabase.getDatabase(requireContext()))
            .enableLogDebug(false)
            .backupIsEncrypted(false)
            .backupLocation(RoomBackup.BACKUP_FILE_LOCATION_CUSTOM_DIALOG)
            .apply {
                onCompleteListener { success, message, _ ->
                    if (success) {
                        restartApp(Intent(requireContext(), MainActivity::class.java))
                    } else {
                        showSnackBar(
                            "There an error. Error message: $message",
                            Snackbar.LENGTH_SHORT
                        )
                    }
                }
            }
            .backup()
    }

    private fun restore() {
        (activity as MainActivity).backup
            .database(MFMDatabase.getDatabase(requireContext()))
            .enableLogDebug(false)
            .backupIsEncrypted(false)
            .backupLocation(RoomBackup.BACKUP_FILE_LOCATION_CUSTOM_DIALOG)
            .apply {
                onCompleteListener { success, message, _ ->
                    if (success) {
                        restartApp(Intent(requireContext(), MainActivity::class.java))
                    } else {
                        showSnackBar(
                            "There an error. Error message: $message",
                            Snackbar.LENGTH_SHORT
                        )
                    }
                }
            }
            .restore()
    }

    private fun showSnackBar(text: String, length: Int) {
        Snackbar.make(binding.root, text, length).show()
    }
}