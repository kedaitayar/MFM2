package io.github.kedaitayar.mfm.ui.budget

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
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

        val adapter = BudgetListAdapter()
        if (budgetType == -1) {
            setupRecyclerViewData(budgetViewModel.monthlyBudgetListData, adapter)
        } else {
            setupRecyclerViewData(budgetViewModel.monthlyBudgetListData, adapter)
        }

        return binding.root
    }

    private fun setupRecyclerViewData(
        livedata: LiveData<List<BudgetListAdapterData>>,
        adapter: BudgetListAdapter
    ) {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        livedata.observe(viewLifecycleOwner, Observer {
            it?.let {
                for (item in it) {
                    Log.i(TAG, "setupRecyclerViewData: $it")
                }
                adapter.submitList(it)
            }
        })
        popupMenuSetup(adapter)
    }

    private fun popupMenuSetup(adapter: BudgetListAdapter) {
        adapter.setOnItemClickListener(object : BudgetListAdapter.OnItemClickListener {

            override fun onPopupMenuButtonClick(
                budgetListAdapterData: BudgetListAdapterData,
                popupMenuButton: Button
            ) {
                val popupMenu = PopupMenu(this@BudgetListFragment.context, popupMenuButton)
                popupMenu.inflate(R.menu.menu_budget_list_item)
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.edit -> {
                            true
                        }
                        R.id.detail -> {
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
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