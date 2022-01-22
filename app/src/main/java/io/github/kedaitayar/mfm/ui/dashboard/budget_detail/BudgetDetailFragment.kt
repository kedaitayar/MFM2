package io.github.kedaitayar.mfm.ui.dashboard.budget_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.*
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.podata.BudgetTransactionAmountList
import io.github.kedaitayar.mfm.databinding.FragmentBudgetDetailBinding
import io.github.kedaitayar.mfm.databinding.RecyclerViewItemBudgetTransactionAmountListBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.DecimalFormat


@AndroidEntryPoint
class BudgetDetailFragment : Fragment(R.layout.fragment_budget_detail) {
    private val binding: FragmentBudgetDetailBinding by viewBinding()
    private val budgetDetailViewModel: BudgetDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = BudgetTransactionAmountListAdapter()
        binding.apply {
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )

            chipGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    chip1Month.id -> {
                        budgetDetailViewModel.filterChip.value =
                            BudgetDetailViewModel.TimeRangeChip.OneMonth
                    }
                    chip3Months.id -> {
                        budgetDetailViewModel.filterChip.value =
                            BudgetDetailViewModel.TimeRangeChip.ThreeMonth
                    }
                    chip1Year.id -> {
                        budgetDetailViewModel.filterChip.value =
                            BudgetDetailViewModel.TimeRangeChip.OneYear
                    }
                    chipAllTime.id -> {
                        budgetDetailViewModel.filterChip.value =
                            BudgetDetailViewModel.TimeRangeChip.AllTime
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    budgetDetailViewModel.budgetTransactionAmountList.collect {
                        Timber.i(it.toString())
                        adapter.submitList(it)
                    }
                }
            }
        }
    }
}

private class BudgetTransactionAmountListAdapter :
    ListAdapter<BudgetTransactionAmountList, BudgetTransactionAmountListAdapter.BudgetTransactionListViewHolder>(
        BudgetTransactionAmountListDiffCallback()
    ) {
//    private val colors = ArrayList<Int>()
//
//    init {
//        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
//        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
//        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
//        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
//        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
//        colors.add(ColorTemplate.getHoloBlue())
//    }

    inner class BudgetTransactionListViewHolder(private val binding: RecyclerViewItemBudgetTransactionAmountListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BudgetTransactionAmountList, position: Int) {
//            binding.budgetColor.background.setTint(colors[position])
            binding.textViewBudget.text = item.budgetName
            binding.textViewTransactionAmount.text = "${item.transactionAmount}"
            val decFormat = DecimalFormat("##.#")
            binding.textViewPercentage.text =
                "${
                    decFormat.format((item.transactionAmount / item.totalTransactionAmount) * 100)
                }%"
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BudgetTransactionListViewHolder {
        return BudgetTransactionListViewHolder(
            RecyclerViewItemBudgetTransactionAmountListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: BudgetTransactionListViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

}

private class BudgetTransactionAmountListDiffCallback :
    DiffUtil.ItemCallback<BudgetTransactionAmountList>() {
    override fun areItemsTheSame(
        oldItem: BudgetTransactionAmountList,
        newItem: BudgetTransactionAmountList
    ): Boolean {
        return oldItem.budgetId == newItem.budgetId
    }

    override fun areContentsTheSame(
        oldItem: BudgetTransactionAmountList,
        newItem: BudgetTransactionAmountList
    ): Boolean {
        return oldItem == newItem
    }

}