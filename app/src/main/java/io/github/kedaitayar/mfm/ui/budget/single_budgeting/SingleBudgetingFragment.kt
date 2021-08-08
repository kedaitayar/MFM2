package io.github.kedaitayar.mfm.ui.budget.single_budgeting

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.marginBottom
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.podata.BudgetListAdapterData
import io.github.kedaitayar.mfm.databinding.FragmentSingleBudgetingBinding
import io.github.kedaitayar.mfm.ui.main.MainViewModel
import io.github.kedaitayar.mfm.ui.transaction.add_edit_transaction.utils.BudgetListArrayAdapter
import io.github.kedaitayar.mfm.util.SoftKeyboardManager.hideKeyboard
import io.github.kedaitayar.mfm.util.dpToPx
import io.github.kedaitayar.mfm.util.toCurrency
import io.github.kedaitayar.mfm.util.toDp
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "SingleBudgetingFragment"

//R.layout.fragment_single_budgeting
@AndroidEntryPoint
class SingleBudgetingFragment : BottomSheetDialogFragment() {
    private val singleBudgetingViewModel: SingleBudgetingViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentSingleBudgetingBinding? = null
    private val binding get() = _binding!!
    private var animatorMargin: ValueAnimator? = null
    private var animatorAlpha: ValueAnimator? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSingleBudgetingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLabel()
        setupKeypad()
        setupChipsListener()
        setupBudgetDropdown()
        setupSubmit()
        setupObserve()
        setupEventListener()
    }

    private fun setupLabel() {
        binding.textViewBudgetName.text = singleBudgetingViewModel.budget?.budgetName
        binding.textViewBudgetBudgetedGoal.text =
            "${singleBudgetingViewModel.budget?.budgetAllocation} / ${singleBudgetingViewModel.budget?.budgetGoal}"
    }

    private fun setupEventListener() {
        lifecycleScope.launchWhenResumed {
            singleBudgetingViewModel.singleBudgetingEvent.collect {
                when (it) {
                    is SingleBudgetingViewModel.SingleBudgetingEvent.NavigateBackWithResult -> {
                        if (it.result) {
                            mainViewModel.showSnackbar("Budget updated", Snackbar.LENGTH_SHORT)
                        } else {
                            mainViewModel.showSnackbar("Budget update failed", Snackbar.LENGTH_SHORT)
                        }
                        hideKeyboard()
                        findNavController().navigateUp()
                    }
                    is SingleBudgetingViewModel.SingleBudgetingEvent.InvalidInput -> {
                        binding.textInputLayoutBudget.error = it.budgetErrorMessage
                        binding.textInputLayoutAmount.error = it.amountErrorMessage
                    }
                }
            }
        }
    }

    private fun setupObserve() {
        lifecycleScope.launchWhenResumed {
            singleBudgetingViewModel.inputChip.collect {
                when (it) {
                    SingleBudgetingViewModel.SingleBudgetingType.ADD -> {

                        animateBudgetInput(false)
                    }
                    SingleBudgetingViewModel.SingleBudgetingType.MINUS -> {
                        animateBudgetInput(false)
                    }
                    SingleBudgetingViewModel.SingleBudgetingType.GIVE -> {
                        animateBudgetInput(true)
                    }
                    SingleBudgetingViewModel.SingleBudgetingType.TAKE -> {
                        animateBudgetInput(true)
                    }
                }
            }
        }
        lifecycleScope.launchWhenResumed {
            singleBudgetingViewModel.budgetLabel.collect {
                binding.textInputLayoutBudget.hint = it
            }
        }
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

        lifecycleScope.launch {
            singleBudgetingViewModel.inputAmount.collect {
                binding.textInputEditAmount.setText(it)
                binding.textInputLayoutAmount.error = null
            }
        }
    }

    private fun setupChipsListener() {
        binding.chipGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.chipAdd.id -> {
                    singleBudgetingViewModel.inputChip.value = SingleBudgetingViewModel.SingleBudgetingType.ADD
                }
                binding.chipMinus.id -> {
                    singleBudgetingViewModel.inputChip.value = SingleBudgetingViewModel.SingleBudgetingType.MINUS
                }
                binding.chipGive.id -> {
                    singleBudgetingViewModel.inputChip.value = SingleBudgetingViewModel.SingleBudgetingType.GIVE
                }
                binding.chipTake.id -> {
                    singleBudgetingViewModel.inputChip.value = SingleBudgetingViewModel.SingleBudgetingType.TAKE
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
            binding.textInputLayoutBudget.error = null
        }
    }

    private fun setupSubmit() {
        binding.buttonSubmit.setOnClickListener {
            singleBudgetingViewModel.onSubmit()
        }
    }

    private fun animateBudgetInput(isExpanded: Boolean) {
        val animationDuration = 300L
        val targetMargin = if (isExpanded) 72f else 0f
        val amount = binding.textInputLayoutAmount
        val budget = binding.textInputLayoutBudget
        val targetAlpha = if (isExpanded) 1f else 0f
        val alphaDelay = if (isExpanded) (animationDuration * 0.8).toLong() else 0

        if (amount.marginBottom.toDp.toFloat() == targetMargin) return

        ValueAnimator.ofFloat(amount.marginBottom.toDp.toFloat(), targetMargin).apply {
            animatorMargin?.cancel()
            animatorMargin = this
            duration = animationDuration
            start()
            addUpdateListener {
                val param = amount.layoutParams as ViewGroup.MarginLayoutParams
                param.bottomMargin = requireContext().dpToPx(it.animatedValue as Float)
                amount.layoutParams = param
            }
        }
        ValueAnimator.ofFloat(budget.alpha, targetAlpha).apply {
            animatorAlpha?.cancel()
            animatorAlpha = this
            duration = (animationDuration * 0.2).toLong()
            startDelay = alphaDelay
            start()

            addUpdateListener {
                budget.alpha = it.animatedValue as Float
            }

            doOnStart {
                if (isExpanded) budget.visibility = View.VISIBLE
            }

            doOnEnd {
                if (!isExpanded) budget.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
