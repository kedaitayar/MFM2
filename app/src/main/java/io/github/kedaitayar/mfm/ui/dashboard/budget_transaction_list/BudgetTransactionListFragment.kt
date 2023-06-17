package io.github.kedaitayar.mfm.ui.dashboard.budget_transaction_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.podata.BudgetTransactionAmountList
import io.github.kedaitayar.mfm.databinding.FragmentBudgetTransactionListBinding
import io.github.kedaitayar.mfm.databinding.RecyclerViewItemBudgetTransactionAmountListBinding
import io.github.kedaitayar.mfm.util.safeCollection
import java.text.DecimalFormat


@AndroidEntryPoint
class BudgetTransactionListFragment : Fragment(R.layout.fragment_budget_transaction_list) {
    private val binding: FragmentBudgetTransactionListBinding by viewBinding()
    private val budgetTransactionListViewModel: BudgetTransactionListViewModel by viewModels()

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

            chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
                when (checkedIds.firstOrNull()) {
                    chip1Month.id -> {
                        budgetTransactionListViewModel.filterChip.value =
                            BudgetTransactionListViewModel.TimeRangeChip.OneMonth
                    }

                    chip3Months.id -> {
                        budgetTransactionListViewModel.filterChip.value =
                            BudgetTransactionListViewModel.TimeRangeChip.ThreeMonth
                    }

                    chip1Year.id -> {
                        budgetTransactionListViewModel.filterChip.value =
                            BudgetTransactionListViewModel.TimeRangeChip.OneYear
                    }

                    chipAllTime.id -> {
                        budgetTransactionListViewModel.filterChip.value =
                            BudgetTransactionListViewModel.TimeRangeChip.AllTime
                    }
                }
            }
        }

        budgetTransactionListViewModel.budgetTransactionAmountList.safeCollection(viewLifecycleOwner) {
            adapter.submitList(it)
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

        fun bind(item: BudgetTransactionAmountList) {
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
        holder.bind(getItem(position))
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