package io.github.kedaitayar.mfm.ui.budget

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.entity.BudgetTransaction
import io.github.kedaitayar.mfm.databinding.FragmentBudgetingBinding
import io.github.kedaitayar.mfm.viewmodels.BudgetViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.format.TextStyle
import java.util.*

private const val TAG = "BudgetingFragment"

@AndroidEntryPoint
class BudgetingFragment : Fragment(R.layout.fragment_budgeting) {
    private val budgetViewModel: BudgetViewModel by viewModels()
    private var _binding: FragmentBudgetingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetingBinding.inflate(inflater, container, false)
        context ?: return binding.root
        val adapter = BudgetingListAdapter()
        setupToolbar(adapter)
        setupBudgetingListRecyclerView(adapter)

        return binding.root
    }

    private fun setupToolbar(adapter: BudgetingListAdapter) {
        binding.topAppBar.inflateMenu(R.menu.menu_budgeting)
        binding.topAppBar.setNavigationIcon(R.drawable.ic_baseline_close_24)
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.save_budgeting -> {
                    val budgetingList = adapter.currentList
                    val budgetingAmountList = adapter.getBudgetingAmountList()
                    val date = budgetViewModel.selectedDate.value!!
                    for ((index, budgeting) in budgetingList.withIndex()) {
                        if (budgeting.budgetTypeId != 2L) {
                            val budgetTransaction = BudgetTransaction(
                                budgetTransactionAmount = ((budgetingAmountList[index])?.toDouble() ?: 0.0),
                                budgetTransactionBudgetId = budgeting.budgetId,
                                budgetTransactionMonth = date.monthValue,
                                budgetTransactionYear = date.year
                            )
                            CoroutineScope(Dispatchers.IO).launch {
                                budgetViewModel.insert(budgetTransaction)
                            }
                        }
                    }
                    findNavController().navigateUp()
                    true
                }
                else -> {
                    false
                }
            }
        }

        budgetViewModel.selectedDate.observe(viewLifecycleOwner, Observer {
            it?.let {
                Log.i(TAG, "setupToolbar selectedDate: $it")
                binding.topAppBar.title = it.month.getDisplayName(
                    TextStyle.FULL,
                    Locale.ENGLISH
                ) + ", " + it.year    //TODO: extract string
            }
        })
    }

    private fun setupBudgetingListRecyclerView(adapter: BudgetingListAdapter) {
        binding.recyclerViewBudgeting.adapter = adapter
        binding.recyclerViewBudgeting.layoutManager = LinearLayoutManager(requireContext())
        budgetViewModel.budgetingListData.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
                for (item in it) {
                    Log.i(TAG, "setupBudgetingListRecyclerView: $item")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}