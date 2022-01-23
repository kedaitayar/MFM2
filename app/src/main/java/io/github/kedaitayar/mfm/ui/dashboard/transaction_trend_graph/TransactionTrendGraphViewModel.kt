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
    var colorPrimary = 0

    init {
        setupTransactionGraphData()
    }

    private fun setupTransactionGraphData() {
        viewModelScope.launch {
            transactionYearlyTrendGraphFlow.collect {
                val barEntries = mutableListOf<BarEntry>()
                val lineEntries = mutableListOf<Entry>()
                val lineEntries2 = mutableListOf<Entry>()
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

                val tempLineData = mutableListOf<Float>()

                for (week in 0 until 54) {
                    val barEntry = BarEntry(
                        week.toFloat(),
                        floatArrayOf(
                            dataMapIncome[week]?.transactionAmount?.toFloat() ?: 0f,
                            -(dataMapExpense[week]?.transactionAmount?.toFloat() ?: 0f)
                        )
                    )
                    barEntries.add(barEntry)
                    total = total + (dataMapIncome[53 - week]?.transactionAmount
                        ?: 0.0) - (dataMapExpense[53 - week]?.transactionAmount ?: 0.0)
                    tempLineData.add(total.toFloat())
                }

                tempLineData.reversed().forEachIndexed { index, fl ->
                    lineEntries.add(Entry((index).toFloat(), fl))
                }
                val movingAverage = movingAverage(tempLineData, 12) { weightedMean() }

                movingAverage.reversed().forEachIndexed { index, fl ->
                    lineEntries2.add(Entry((index).toFloat(), fl))
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
                    lineWidth = 1f
                    setDrawCircles(false)
                }
                val lineDataSet2 = LineDataSet(lineEntries2, "line label")
                lineDataSet2.apply {
                    setDrawValues(false)
                    mode = LineDataSet.Mode.HORIZONTAL_BEZIER
                    color = colorPrimary
                    lineWidth = 1f
                    enableDashedLine(10f, 15f, 0f)
                    setDrawCircles(false)
                }
                val lineData = LineData(lineDataSet, lineDataSet2)

                val combinedData = CombinedData().apply {
                    setData(barData)
                    setData(lineData)
                }

                transactionGraphCombinedData.value = combinedData
            }
        }
    }

}

fun <T> List<T>.slidingWindow(size: Int): List<List<T>> {
    if (size < 1) {
        throw IllegalArgumentException("Size must be > 0, but is $size.")
    }
    return this.mapIndexed { index, _ ->
        this.subList(maxOf(index - size + 1, 0), index + 1)
    }
}

fun Iterable<Float>.mean(): Float {
    val sum: Float = this.sum()
    return sum / this.count()
}

fun sumTo(n: Int): Int = n * (n + 1) / 2

fun Iterable<Float>.weightedMean(): Float {
    val sum: Float = this
        .mapIndexed { index, t -> t * (index + 1) }
        .sum()
    return sum / sumTo(this.count())
}

fun movingAverage(
    entries: List<Float>, window: Int,
    averageCalc: Iterable<Float>.() -> Float
): List<Float> {
    val result = entries.slidingWindow(size = window)
        .filter { it.isNotEmpty() }
        .map { it -> it.averageCalc() }
        .toList()
    return result
}