package io.github.kedaitayar.mfm.ui.dashboard.account.account_detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.podata.AccountTransactionChartData
import io.github.kedaitayar.mfm.data.repository.DashboardRepository
import io.github.kedaitayar.mfm.data.repository.SelectedDateRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class AccountTransactionGraphViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository,
    private val selectedDateRepository: SelectedDateRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val selectedOffsetDate = selectedDateRepository.selectedOffsetDate
    var account = MutableStateFlow(
        savedStateHandle.get<Account>(AccountDetailFragment.ACCOUNT_STATE_KEY) ?: Account(accountId = -1)
    )
        set(value) {
            field = value
            savedStateHandle.set(AccountDetailFragment.ACCOUNT_STATE_KEY, value.value)
        }

    private val accountTransactionChartDataFlow = account.combine(selectedOffsetDate) { account, date ->
        getAccountTransactionChartData(account.accountId, date)
    }.flattenMerge()

    val accountTransactionGraphCombinedData = MutableLiveData<CombinedData>()
    var green = 0
    var red = 0
    var colorOnSurface = 0


    init {
        setupGraphData()
    }

    private fun setupGraphData() {
        viewModelScope.launch {
            accountTransactionChartDataFlow.collect { list ->
                val dataMap: MutableMap<Int, AccountTransactionChartData> = mutableMapOf()
                val barEntries = mutableListOf<BarEntry>()
                val lineEntries = mutableListOf<Entry>()

                //map data to its day
                for (item in list) {
                    dataMap[item.accountTransactionDate.substring(8).toInt()] = item
                }

                // for line graph
                var total =
                    (list.firstOrNull()?.accountTransactionIncomePrevMonth ?: 0.0) -
                            (list.firstOrNull()?.accountTransactionExpensePrevMonth ?: 0.0) -
                            (list.firstOrNull()?.accountTransactionTransferOutPrevMonth ?: 0.0) +
                            (list.firstOrNull()?.accountTransactionTransferInPrevMonth ?: 0.0)

                // insert data to both entries according to its day
//                for (day in 1 until OffsetDateTime.now().month.maxLength()) {
//                    val moneyIn: Double = (dataMap[day]?.accountTransactionIncome ?: 0.0) +
//                            (dataMap[day]?.accountTransactionTransferIn ?: 0.0)
//                    val moneyOut: Double = (dataMap[day]?.accountTransactionExpense ?: 0.0) +
//                            (dataMap[day]?.accountTransactionTransferOut ?: 0.0)
//                    barEntries.add(
//                        BarEntry(
//                            day - 1f,
//                            floatArrayOf(moneyIn.toFloat(), -moneyOut.toFloat())
//                        )
//                    )
//                    total += moneyIn
//                    total -= moneyOut
//                    lineEntries.add(Entry(day - 1f, total.toFloat()))
//                }

                for (item in list) {
                    val moneyIn = item.accountTransactionIncome + item.accountTransactionTransferIn
                    val moneyOut = item.accountTransactionExpense + item.accountTransactionTransferOut
                    val day = LocalDate.parse(item.accountTransactionDate).dayOfMonth.toFloat()
                    barEntries.add(
                        BarEntry(
                            day,
                            floatArrayOf(moneyIn.toFloat(), -moneyOut.toFloat())
                        )
                    )
                    total += moneyIn
                    total -= moneyOut
                    lineEntries.add(Entry(day - 1f, total.toFloat()))
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
                    mode = LineDataSet.Mode.LINEAR
                    color = colorOnSurface
                    setCircleColor(colorOnSurface)
                    lineWidth = 2f
                    circleRadius = 2f
                }
                val lineData = LineData(lineDataSet)

                val combinedData = CombinedData().apply {
                    setData(barData)
                    setData(lineData)
                }

                accountTransactionGraphCombinedData.value = combinedData
            }
        }
    }

    private fun getAccountTransactionChartData(
        accountId: Long,
        dateTime: OffsetDateTime
    ): Flow<List<AccountTransactionChartData>> {
        return dashboardRepository.getAccountTransactionChartData(accountId, dateTime.monthValue, dateTime.year)
    }
}