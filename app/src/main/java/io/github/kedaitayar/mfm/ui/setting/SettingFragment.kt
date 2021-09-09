package io.github.kedaitayar.mfm.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.ebner.roomdatabasebackup.core.RoomBackup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import io.github.kedaitayar.mfm.MainActivity
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.database.MFMDatabase
import io.github.kedaitayar.mfm.databinding.FragmentSettingBinding
import io.github.kedaitayar.mfm.ui.main.MainViewModel
import timber.log.Timber

class SettingFragment : Fragment(R.layout.fragment_setting) {
    private val mainViewModel: MainViewModel by activityViewModels()
    val binding: FragmentSettingBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

        binding.restore.setOnClickListener {
            restore()
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
}