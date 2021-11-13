package io.github.kedaitayar.mfm.ui.transaction.quick_transaction

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.repository.BasicRepository
import io.github.kedaitayar.mfm.data.repository.QuickTransactionRepository
import javax.inject.Inject

@HiltViewModel
class QuickTransactionSelectionViewModel @Inject constructor(
        quickTransactionRepository: QuickTransactionRepository,
) : ViewModel() {
    val allQuickTransaction = quickTransactionRepository.getAllQuickTransaction()
}