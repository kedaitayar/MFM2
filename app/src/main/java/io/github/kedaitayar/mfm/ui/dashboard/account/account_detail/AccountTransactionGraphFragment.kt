package io.github.kedaitayar.mfm.ui.dashboard.account.account_detail

import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.databinding.FragmentAccountTransactionGraphBinding

private const val ARG_ACCOUNT =
    "io.github.kedaitayar.mfm.ui.dashboard.account.account_detail.AccountTransactionGraphFragment.account"

@AndroidEntryPoint
class AccountTransactionGraphFragment : Fragment(R.layout.fragment_account_transaction_graph) {
    private val accountTransactionGraphViewModel: AccountTransactionGraphViewModel by viewModels()
    private var _binding: FragmentAccountTransactionGraphBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            accountTransactionGraphViewModel.account.value = it.getParcelable(ARG_ACCOUNT) ?: Account(accountId = -1)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAccountTransactionGraphBinding.bind(view)

        initViewModelColor()
        setupCombinedGraph()
    }

    private fun initViewModelColor() {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.gGreen, typedValue, true)
        accountTransactionGraphViewModel.green = ContextCompat.getColor(requireContext(), typedValue.resourceId)
        requireContext().theme.resolveAttribute(R.attr.gRed, typedValue, true)
        accountTransactionGraphViewModel.red = ContextCompat.getColor(requireContext(), typedValue.resourceId)
        requireContext().theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true)
        accountTransactionGraphViewModel.colorOnSurface = ContextCompat.getColor(requireContext(), typedValue.resourceId)
    }

    private fun setupCombinedGraph() {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true)
        val colorOnSurface = ContextCompat.getColor(requireContext(), typedValue.resourceId)

        binding.combinedChart.apply {
            description.isEnabled = false
            drawOrder = arrayOf(
                CombinedChart.DrawOrder.BAR,
                CombinedChart.DrawOrder.BUBBLE,
                CombinedChart.DrawOrder.CANDLE,
                CombinedChart.DrawOrder.LINE,
                CombinedChart.DrawOrder.SCATTER
            )
            setDrawGridBackground(false)
            legend.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTH_SIDED
            xAxis.textColor = colorOnSurface
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return when (value) {
                        0f -> {
                            ""
                        }
                        else -> {
                            "day ${value.toInt()}"
                        }
                    }
                }
            }
            extraBottomOffset = 5f
            extraTopOffset = 5f
            axisLeft.setDrawGridLines(false)
            axisLeft.setDrawZeroLine(true)
            axisLeft.textColor = colorOnSurface
            axisRight.setDrawGridLines(false)
            axisRight.textColor = colorOnSurface
        }

        accountTransactionGraphViewModel.accountTransactionGraphCombinedData.observe(viewLifecycleOwner) {
            it?.let {
                binding.combinedChart.apply {
                    data = it
                    notifyDataSetChanged()
                    invalidate()
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
        fun newInstance(account: Account) =
            AccountTransactionGraphFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_ACCOUNT, account)
                }
            }
    }
}