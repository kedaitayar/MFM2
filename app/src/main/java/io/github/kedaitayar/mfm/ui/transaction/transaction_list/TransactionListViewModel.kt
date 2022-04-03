package io.github.kedaitayar.mfm.ui.transaction.transaction_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.repository.TransactionRepository
import javax.inject.Inject

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    transactionRepository: TransactionRepository
) : ViewModel() {
    val allTransactionListAdapterData =
        transactionRepository.getTransactionListData().cachedIn(viewModelScope)
}