package io.github.kedaitayar.mfm.ui.dashboard.spending

import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.repository.TransactionRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MonthlySpendingViewModel @Inject constructor(transactionRepository: TransactionRepository) :
    ViewModel() {
    private val monthlySpending = transactionRepository.getMonthlySpendingGraphData()
    val monthlySpendingGraph = monthlySpending.map { raw ->
        val barEntries = mutableListOf<BarEntry>()
        raw.forEachIndexed { index, monthlySpendingData ->
            barEntries.add(
                BarEntry(
                    index.toFloat(),
                    monthlySpendingData.monthSpending.toFloat()
                )
            )
        }
        barEntries
    }
}