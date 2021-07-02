package io.github.kedaitayar.mfm.ui.budget.add_edit_budget

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentAddBudgetBinding
import io.github.kedaitayar.mfm.data.entity.BudgetType
import io.github.kedaitayar.mfm.util.SoftKeyboardManager.hideKeyboard
import io.github.kedaitayar.mfm.ui.main.MainViewModel
import io.github.kedaitayar.mfm.util.exhaustive
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddBudgetFragment : Fragment(R.layout.fragment_add_budget) {
    private val addEditBudgetViewModel: AddEditBudgetViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentAddBudgetBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddBudgetBinding.bind(view)

        setupEventListener()
        setupInputListener()
        setupDropdown()
        setupToolbar()
        setupAddBudgetButton()

    }

    private fun setupEventListener() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            addEditBudgetViewModel.addEditBudgetEvent.collect { event ->
                when (event) {
                    is AddEditBudgetViewModel.AddEditBudgetEvent.NavigateBackWithAddResult -> {
                        if (event.result > 0L) {
                            mainViewModel.showSnackbar("Budget added", Snackbar.LENGTH_SHORT)
                        } else {
                            mainViewModel.showSnackbar("Budget add failed", Snackbar.LENGTH_SHORT)
                        }
                        hideKeyboard()
                        findNavController().navigateUp()
                    }
                    is AddEditBudgetViewModel.AddEditBudgetEvent.NavigateBackWithDeleteResult -> {
                        if (event.result == 1) {
                            mainViewModel.showSnackbar("Budget deleted", Snackbar.LENGTH_SHORT)
                        } else {
                            mainViewModel.showSnackbar("Budget delete failed", Snackbar.LENGTH_SHORT)
                        }
                        hideKeyboard()
                        findNavController().navigateUp()
                    }
                    is AddEditBudgetViewModel.AddEditBudgetEvent.NavigateBackWithEditResult -> {
                        if (event.result == 1) {
                            mainViewModel.showSnackbar("Budget updated", Snackbar.LENGTH_SHORT)
                        } else {
                            mainViewModel.showSnackbar("Budget update failed", Snackbar.LENGTH_SHORT)
                        }
                        hideKeyboard()
                        findNavController().navigateUp()
                    }
                    is AddEditBudgetViewModel.AddEditBudgetEvent.ShowSnackbar -> {
                        Snackbar.make(requireView(), event.msg, event.length)
                            .setAnchorView(binding.buttonAddBudget)
                            .show()
                    }
                }.exhaustive
            }
        }
    }

    private fun setupInputListener() {
        binding.apply {
            textInputEditBudgetName.addTextChangedListener {
                addEditBudgetViewModel.inputBudgetName = it.toString()
            }
            textInputEditBudgetGoal.addTextChangedListener {
                addEditBudgetViewModel.inputBudgetGoal = it.toString().toDoubleOrNull()
            }
            autoCompleteBudgetType.setOnItemClickListener { parent, view, position, id ->
                addEditBudgetViewModel.inputBudgetType = parent.getItemAtPosition(position) as BudgetType?
            }
        }
    }

    private fun setupAddBudgetButton() {
        binding.buttonAddBudget.setOnClickListener {
            addEditBudgetViewModel.onAddClick()
        }
    }

    private fun setupToolbar() {
        binding.topAppBar.setNavigationIcon(R.drawable.ic_baseline_close_24)
        binding.topAppBar.title = "Add Budget"
        binding.topAppBar.setNavigationOnClickListener {
            hideKeyboard()
            findNavController().navigateUp()
        }
    }

    private fun setupDropdown() {
        addEditBudgetViewModel.allBudgetType.observe(viewLifecycleOwner) { list ->
            list.let {
                val adapter = object : ArrayAdapter<BudgetType>(
                    requireContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    it
                ) {
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val currentItemView = convertView ?: LayoutInflater.from(context)
                            .inflate(R.layout.support_simple_spinner_dropdown_item, parent, false)
                        val budgetType: BudgetType? = getItem(position)
                        val textView = currentItemView as TextView
                        budgetType?.let {
                            textView.text = budgetType.budgetTypeName
                        }
                        return currentItemView
                    }

                    override fun getFilter(): Filter {
                        return object : Filter() {
                            override fun performFiltering(constraint: CharSequence?): FilterResults {
                                val result = FilterResults()
                                result.values = it
                                result.count = it.size
                                return result
                            }

                            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                                if (results != null) {
                                    if (results.count > 0) {
                                        notifyDataSetChanged()
                                    } else {
                                        notifyDataSetInvalidated()
                                    }
                                }
                            }

                            override fun convertResultToString(resultValue: Any?): CharSequence {
                                if (resultValue is BudgetType) {
                                    return resultValue.budgetTypeName
                                }
                                return resultValue.toString()
                            }
                        }
                    }
                }
                binding.autoCompleteBudgetType.setAdapter(adapter)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}