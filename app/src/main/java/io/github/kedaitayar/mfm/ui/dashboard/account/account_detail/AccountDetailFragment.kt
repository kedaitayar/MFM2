package io.github.kedaitayar.mfm.ui.dashboard.account.account_detail

import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentAccountDetailBinding
import io.github.kedaitayar.mfm.ui.transaction.transaction_list.TransactionListAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountDetailFragment : Fragment(R.layout.fragment_account_detail) {
    private val binding: FragmentAccountDetailBinding by viewBinding()
    private val args: AccountDetailFragmentArgs by navArgs()
    private val accountDetailViewModel: AccountDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModelColor()
        setupCombinedGraph()

        val adapter = TransactionListAdapter()
        binding.apply {
            recyclerViewTransactionList.adapter = adapter
            recyclerViewTransactionList.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewTransactionList.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                accountDetailViewModel.transactionListByAccount.collect {
                    adapter.submitData(it)
                }
            }
        }

        setupTopAppBar()
    }

    private fun setupTopAppBar() {
        binding.topAppBar.apply {
            setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            title = "${args.account.accountName} Detail"
        }
    }

    private fun initViewModelColor() {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.gGreen, typedValue, true)
        accountDetailViewModel.green = ContextCompat.getColor(requireContext(), typedValue.resourceId)
        requireContext().theme.resolveAttribute(R.attr.gRed, typedValue, true)
        accountDetailViewModel.red = ContextCompat.getColor(requireContext(), typedValue.resourceId)
        requireContext().theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true)
        accountDetailViewModel.colorOnSurface = ContextCompat.getColor(requireContext(), typedValue.resourceId)
        requireContext().theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        accountDetailViewModel.colorPrimary = ContextCompat.getColor(requireContext(), typedValue.resourceId)
    }

    private fun setupCombinedGraph() {
        binding.combinedChart.apply {
            setTouchEnabled(false)
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
//            xAxis.setDrawGridLines(false)
            val typedValue = TypedValue()
            requireContext().theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true)
            val colorOnSurface = ContextCompat.getColor(requireContext(), typedValue.resourceId)
            xAxis.textColor = colorOnSurface
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    if (value == 0f) {
                        return "now"
                    }
                    return "week ${value.toInt()}"
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

        accountDetailViewModel.transactionGraphCombinedData.observe(viewLifecycleOwner) {
            it?.let {
                binding.combinedChart.apply {
                    data = it
                    notifyDataSetChanged()
                    invalidate()
                }
            }
        }
    }

    companion object {
        const val ACCOUNT_STATE_KEY =
            "io.github.kedaitayar.mfm.ui.dashboard.account.account_detail.account"
    }
}