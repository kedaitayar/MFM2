package io.github.kedaitayar.mfm.ui.budget

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.podata.BudgetListAdapterData
import io.github.kedaitayar.mfm.databinding.FragmentBudgetListBinding
import io.github.kedaitayar.mfm.viewmodels.BudgetViewModel

private const val TAG = "BudgetListFragment"
private const val ARG_BUDGET_TYPE =
    "io.github.kedaitayar.mfm.ui.budget.BudgetListFragment.budgetType"

@AndroidEntryPoint
class BudgetListFragment : Fragment() {
    private val budgetViewModel: BudgetViewModel by viewModels()
    private var _binding: FragmentBudgetListBinding? = null
    private val binding get() = _binding!!
    private var budgetType: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            budgetType = it.getInt(ARG_BUDGET_TYPE, -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBudgetListBinding.inflate(inflater, container, false)
        context ?: return binding.root

        val recyclerView = binding.recyclerView
        val adapter = BudgetListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        budgetViewModel.selectedDate.observe(viewLifecycleOwner, Observer {
            it?.let {
                Log.i(TAG, "observe selectedDate: $it")
            }
        })

        /// temp,
        if (budgetType == -1) {
            budgetViewModel.allBudget.observe(viewLifecycleOwner, Observer {
                it?.let { list ->
                    val recyclerList: List<BudgetListAdapterData> = list.map { item ->
                        BudgetListAdapterData(budgetId = item.budgetId!!, budgetName = item.budgetName)
                    }
                    adapter.submitList(recyclerList)
                }
            })
        }

        return binding.root
    }

    private fun setupRecyclerViewData(
        livedata: LiveData<List<BudgetListAdapterData>>,
        adapter: BudgetListAdapter
    ) {
        livedata.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(budgetType: Int) =
            BudgetListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_BUDGET_TYPE, budgetType)
                }
            }
    }
}