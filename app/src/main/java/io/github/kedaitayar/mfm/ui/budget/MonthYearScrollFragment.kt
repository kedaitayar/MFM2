package io.github.kedaitayar.mfm.ui.budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentMonthYearScrollBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.format.TextStyle
import java.util.*

@AndroidEntryPoint
class MonthYearScrollFragment : Fragment(R.layout.fragment_month_year_scroll) {
    private val monthYearScrollViewModel: MonthYearScrollViewModel by viewModels()
    private val binding: FragmentMonthYearScrollBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                monthYearScrollViewModel.selectedDate.collect {
                    binding.buttonDate.text =
                        it.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH) + ", " + it.year
                }
            }
        }

        binding.buttonLeft.setOnClickListener {
            monthYearScrollViewModel.decreaseMonth()
        }
        binding.buttonRight.setOnClickListener {
            monthYearScrollViewModel.increaseMonth()
        }
    }
}