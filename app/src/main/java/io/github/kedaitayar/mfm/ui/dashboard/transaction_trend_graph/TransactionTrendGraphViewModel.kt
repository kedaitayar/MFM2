package io.github.kedaitayar.mfm.ui.dashboard.transaction_trend_graph

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.podata.TransactionGraphData
import io.github.kedaitayar.mfm.data.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.temporal.WeekFields
import java.util.*
import javax.inject.Inject
import kotlin.collections.set

@HiltViewModel
class TransactionTrendGraphViewModel @Inject constructor(
    transactionRepository: TransactionRepository
) : ViewModel() {
    private val transactionYearlyTrendGraphFlow: Flow<List<TransactionGraphData>> =
        transactionRepository.getTransactionGraphData()
    val transactionGraphCombinedData = MutableLiveData<CombinedData>()
    var green = 0
    var red = 0
    var colorOnSurface = 0

    init {
        setupTransactionGraphData()
    }

    private fun setupTransactionGraphData() {
        viewModelScope.launch {
            transactionYearlyTrendGraphFlow.collect {
                val barEntries = mutableListOf<BarEntry>()
                val lineEntries = mutableListOf<Entry>()
                val dataMapIncome = mutableMapOf<Int, TransactionGraphData>()
                val dataMapExpense = mutableMapOf<Int, TransactionGraphData>()
                var total: Double
                for (item in it) {
                    when (item.transactionType) {
                        1 -> {
                            dataMapExpense[item.transactionAgeInWeek] = item
                        }
                        2 -> {
                            dataMapIncome[item.transactionAgeInWeek] = item
                        }
                    }
                }
                total = (dataMapIncome[-1]?.transactionAmountPrevYear
                    ?: 0.0) - (dataMapExpense[-1]?.transactionAmountPrevYear ?: 0.0)

                for (week in 0 until 53) {
                    val barEntry = BarEntry(
                        (week + 1).toFloat(),
                        floatArrayOf(
                            dataMapIncome[53 - week]?.transactionAmount?.toFloat() ?: 0f,
                            -(dataMapExpense[53 - week]?.transactionAmount?.toFloat() ?: 0f)
                        )
                    )
                    barEntries.add(barEntry)
                    total = total + (dataMapIncome[53 - week]?.transactionAmount
                        ?: 0.0) - (dataMapExpense[53 - week]?.transactionAmount ?: 0.0)
                    lineEntries.add(Entry((week).toFloat(), total.toFloat()))
                }

                val barDataSet = BarDataSet(barEntries, "bardataset label")
                barDataSet.colors = arrayListOf(
                    green,
                    red
                )
                barDataSet.setDrawValues(false)
                val barData = BarData(barDataSet)

                val lineDataSet = LineDataSet(lineEntries, "line label")
                lineDataSet.apply {
                    setDrawValues(false)
                    mode = LineDataSet.Mode.HORIZONTAL_BEZIER
                    color = colorOnSurface
                    lineWidth = 2f
                    setDrawCircles(false)
                }
                val lineData = LineData(lineDataSet)

                val combinedData = CombinedData().apply {
                    setData(barData)
                    setData(lineData)
                }

                transactionGraphCombinedData.value = combinedData
            }
        }
    }

}