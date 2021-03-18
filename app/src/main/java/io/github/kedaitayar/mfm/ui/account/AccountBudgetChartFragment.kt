package io.github.kedaitayar.mfm.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentAccountBudgetChartBinding
import io.github.kedaitayar.mfm.viewmodels.AccountViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

private const val ARG_ACCOUNT_ID = "io.github.kedaitayar.mfm.ui.account.AccountBudgetChartFragment.accountId"

@AndroidEntryPoint
class AccountBudgetChartFragment : Fragment(R.layout.fragment_account_budget_chart) {
    private val accountViewModel: AccountViewModel by viewModels()
    private var _binding: FragmentAccountBudgetChartBinding? = null
    private val binding get() = _binding!!
    private var accountId = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            accountId = it.getLong(ARG_ACCOUNT_ID, -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBudgetChartBinding.inflate(inflater, container, false)
        context ?: return binding.root

        setupPieChartAndBudgetList()

        return binding.root
    }

    private fun setupPieChartAndBudgetList(){
        val adapter = AccountBudgetChartListAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.pieChart.apply {
            setDrawEntryLabels(false)
//            holeRadius = 60f
            isDrawHoleEnabled = false
            setTouchEnabled(false)
            description.isEnabled = false
        }

        binding.pieChart.legend.apply {
            isEnabled = false
        }

        val entries = arrayListOf<PieEntry>()

        if (accountId != -1L) {
            CoroutineScope(Dispatchers.IO).launch {
                var accountTransactionBudget =
                    accountViewModel.getAccountTransactionBudget(accountId)
                        .sortedByDescending { it.transactionAmount }
                for (item in accountTransactionBudget) {
                    val transactionAmount = (item.transactionAmount ?: 0L) as Float
                    val budgetName = item.budgetName
                    val pieEntry = PieEntry(transactionAmount, budgetName)
                    entries.add(pieEntry)
                }
                val dataSet = PieDataSet(entries, "dataset label")

                // add a lot of colors
                val colors = ArrayList<Int>()
                for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
                for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
                for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
                for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
                for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
                colors.add(ColorTemplate.getHoloBlue())

                dataSet.colors = colors

                val data = PieData(dataSet)
                data.setDrawValues(false)
                binding.pieChart.data = data
                binding.pieChart.invalidate()

                withContext(Dispatchers.Main) {
                    adapter.submitList(accountTransactionBudget)
                    adapter.setTotalTransactionAmount(accountTransactionBudget.sumByDouble {
                        it.transactionAmount?.toDouble() ?: 0.0
                    }.toFloat())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(accountId: Long) =
            AccountBudgetChartFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_ACCOUNT_ID, accountId)
                }
            }
    }
}