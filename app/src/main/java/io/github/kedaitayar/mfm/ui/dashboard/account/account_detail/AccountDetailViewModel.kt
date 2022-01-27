package io.github.kedaitayar.mfm.ui.dashboard.account.account_detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.github.mikephil.charting.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.podata.TransactionGraphData
import io.github.kedaitayar.mfm.data.repository.DashboardRepository
import io.github.kedaitayar.mfm.ui.dashboard.transaction_trend_graph.movingAverage
import io.github.kedaitayar.mfm.ui.dashboard.transaction_trend_graph.weightedMean
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    dashboardRepository: DashboardRepository
) : ViewModel() {
    val account = AccountDetailFragmentArgs.fromSavedStateHandle(savedStateHandle).account
    val transactionGraphCombinedData = MutableLiveData<CombinedData>()
    var green = 0
    var red = 0
    var colorOnSurface = 0
    var colorPrimary = 0

    private val transactionTrendByAccountGraphFlow =
        dashboardRepository.getTransactionByAccountGraphData(account.accountId)
    val transactionListByAccount =
        dashboardRepository.getTransactionListByAccountData(account.accountId)
            .cachedIn(viewModelScope)

    init {
        setupTransactionGraphData()
    }

    private fun setupTransactionGraphData() {
        viewModelScope.launch {
            transactionTrendByAccountGraphFlow.collect {
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