package io.github.kedaitayar.mfm.ui.budget.single_budgeting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.podata.BudgetListAdapterData
import io.github.kedaitayar.mfm.databinding.FragmentSingleBudgetingBinding
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.utils.BudgetListArrayAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "SingleBudgetingFragment"

//R.layout.fragment_single_budgeting
@AndroidEntryPoint
class SingleBudgetingFragment : BottomSheetDialogFragment() {
    private val singleBudgetingViewModel: SingleBudgetingViewModel by viewModels()
    private var _binding: FragmentSingleBudgetingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSingleBudgetingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textViewBudgetName.text = singleBudgetingViewModel.budget?.budgetName
//        binding.textViewBudgetTotalBudgetAmount.text =
//            singleBudgetingViewModel.budget?.budgetAllocation?.toCurrency(requireContext())
        lifecycleScope.launch {
            singleBudgetingViewModel.inputAmount.collect {
                binding.textInputEditAmount.setText(it)
            }
        }
        setupKeypad()
        setupChipsListener()
        setupBudgetDropdown()
    }

    private fun setupKeypad() {
        binding.apply {
            key0.setOnClickListener { singleBudgetingViewModel.inputAmount.value += "0" }
            key1.setOnClickListener { singleBudgetingViewModel.inputAmount.value += "1" }
            key2.setOnClickListener { singleBudgetingViewModel.inputAmount.value += "2" }
            key3.setOnClickListener { singleBudgetingViewModel.inputAmount.value += "3" }
            key4.setOnClickListener { singleBudgetingViewModel.inputAmount.value += "4" }
            key5.setOnClickListener { singleBudgetingViewModel.inputAmount.value += "5" }
            key6.setOnClickListener { singleBudgetingViewModel.inputAmount.value += "6" }
            key7.setOnClickListener { singleBudgetingViewModel.inputAmount.value += "7" }
            key8.setOnClickListener { singleBudgetingViewModel.inputAmount.value += "8" }
            key9.setOnClickListener { singleBudgetingViewModel.inputAmount.value += "9" }
            keyDot.setOnClickListener { singleBudgetingViewModel.inputAmount.value += "." }
            keyBackspace.setOnClickListener {
                singleBudgetingViewModel.inputAmount.value = singleBudgetingViewModel.inputAmount.value.dropLast(1)
            }
        }
    }

    private fun setupChipsListener() {
        binding.chipGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.chipAdd.id -> {
                    singleBudgetingViewModel.inputChip = SingleBudgetingViewModel.SingleBudgetingType.ADD
                    binding.constraintLayout.transitionToStart()
                }
                binding.chipMinus.id -> {
                    singleBudgetingViewModel.inputChip = SingleBudgetingViewModel.SingleBudgetingType.MINUS
                    binding.constraintLayout.transitionToStart()
                }
                binding.chipGive.id -> {
                    singleBudgetingViewModel.inputChip = SingleBudgetingViewModel.SingleBudgetingType.GIVE
                    binding.textInputLayoutBudget.hint = "Give to"
                    binding.constraintLayout.transitionToEnd()
                }
                binding.chipTake.id -> {
                    singleBudgetingViewModel.inputChip = SingleBudgetingViewModel.SingleBudgetingType.TAKE
                    binding.textInputLayoutBudget.hint = "Take from"
                    binding.constraintLayout.transitionToEnd()
                }
                else -> {
                }
            }
        }
    }

    private fun setupBudgetDropdown() {
        singleBudgetingViewModel.allBudget.observe(viewLifecycleOwner) { list ->
            list?.let { list2 ->
                val filteredList = list2.filter {
                    it.budgetId != singleBudgetingViewModel.budget?.budgetId
                }
                val adapter =
                    BudgetListArrayAdapter(
                        requireContext(),
                        R.layout.support_simple_spinner_dropdown_item,
                        filteredList
                    )
                binding.autoCompleteBudget.setAdapter(adapter)
            }
        }
        binding.autoCompleteBudget.setOnItemClickListener { parent, _, position, _ ->
            singleBudgetingViewModel.inputBudget = parent.getItemAtPosition(position) as BudgetListAdapterData?
        }
    }

    private fun setupSubmit() {
        binding.buttonSubmit.setOnClickListener {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}