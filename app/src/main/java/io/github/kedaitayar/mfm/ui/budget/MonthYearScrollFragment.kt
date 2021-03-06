package io.github.kedaitayar.mfm.ui.budget

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentMonthYearScrollBinding
import java.time.format.TextStyle
import java.util.*

@AndroidEntryPoint
class MonthYearScrollFragment : Fragment(R.layout.fragment_month_year_scroll) {
    private val mainBudgetViewModel: MainBudgetViewModel by viewModels()
    private var _binding: FragmentMonthYearScrollBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthYearScrollBinding.inflate(inflater, container, false)
        context ?: return binding.root

        mainBudgetViewModel.selectedDate.observe(viewLifecycleOwner) {
            it?.let {
                binding.buttonDate.text = it.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) + ", " + it.year    //TODO: extract string
            }
        }
        binding.buttonLeft.setOnClickListener {
            mainBudgetViewModel.decreaseMonth()
        }
        binding.buttonRight.setOnClickListener {
            mainBudgetViewModel.increaseMonth()
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}