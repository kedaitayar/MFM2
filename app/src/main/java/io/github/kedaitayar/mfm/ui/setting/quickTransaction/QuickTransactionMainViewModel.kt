package io.github.kedaitayar.mfm.ui.setting.quickTransaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.entity.Transaction
import io.github.kedaitayar.mfm.data.podata.QuickTransactionListAdapterData
import io.github.kedaitayar.mfm.data.repository.QuickTransactionRepository
import io.github.kedaitayar.mfm.ui.dashboard.account.add_edit_account.AddEditAccountFragmentArgs
import javax.inject.Inject

@HiltViewModel
class QuickTransactionMainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    quickTransactionRepository: QuickTransactionRepository,
) : ViewModel() {
    val quickTransactionList = quickTransactionRepository.getQuickTransactionList()
}