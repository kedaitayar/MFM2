package io.github.kedaitayar.mfm.ui.dashboard.budget_detail

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.repository.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class BudgetDetailViewModel @Inject constructor(
    budgetRepository: BudgetRepository
) : ViewModel() {
    val filterChip = MutableStateFlow<TimeRangeChip>(TimeRangeChip.OneMonth)
    val budgetTransactionAmountList = filterChip.flatMapLatest {
        val now = OffsetDateTime.now()
        when (it) {
            TimeRangeChip.AllTime -> budgetRepository.getBudgetTransactionAmountList()
            TimeRangeChip.OneMonth -> budgetRepository.getBudgetTransactionAmountList(
                timeFrom = now.minusMonths(1),
                timeTo = now
            )
            TimeRangeChip.OneYear -> budgetRepository.getBudgetTransactionAmountList(
                timeFrom = now.minusMonths(12),
                timeTo = now
            )
            TimeRangeChip.ThreeMonth -> budgetRepository.getBudgetTransactionAmountList(
                timeFrom = now.minusMonths(3),
                timeTo = now
            )
        }
    }

    sealed class TimeRangeChip {
        object OneMonth : TimeRangeChip()
        object ThreeMonth : TimeRangeChip()
        object OneYear : TimeRangeChip()
        object AllTime : TimeRangeChip()
    }
}