package io.github.kedaitayar.mfm.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentMainBinding
import io.github.kedaitayar.mfm.util.exhaustive
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val binding: FragmentMainBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewPager = binding.viewPagerMain
        viewPager.offscreenPageLimit = 2
        viewPager.adapter = MainViewPagerAdapter(this)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (position == 2) {
                    hideFAB()
                } else {
                    if (!isFABShown()) {
                        showFAB()
                    }
                }
            }
        })

        TabLayoutMediator(binding.tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()

        binding.fab.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToAddTransactionFragment()
            findNavController().navigate(action)
        }

        binding.fab.setOnLongClickListener {
            val action = MainFragmentDirections.actionMainFragmentToQuickTransactionSelectionDialogFragment()
            findNavController().navigate(action)
            true
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            mainViewModel.mainEvent.collect { event ->
                when (event) {
                    is MainViewModel.MainEvent.ShowSnackbar -> {
                        showSnackBar(event.msg, event.length)
                    }
                }.exhaustive
            }
        }

        setupAppBar()
    }

    private fun setupAppBar() {
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.setting -> {
                    val actions = MainFragmentDirections.actionMainFragmentToSettingFragment()
                    findNavController().navigate(actions)
                    true
                }
                else -> false
            }
        }
    }

    fun hideFAB() {
        binding.fab.hide()
    }

    fun showFAB() {
        binding.fab.show()
    }

    fun isFABShown(): Boolean = binding.fab.isShown

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            DASHBOARD_PAGE_INDEX -> "Dashboard"
            TRANSACTION_PAGE_INDEX -> "Transaction"
            BUDGET_PAGE_INDEX -> "Budget"
            else -> null
        }
    }

    private fun showSnackBar(text: String, length: Int) {
        Snackbar.make(binding.coordinatorLayout, text, length).show()
    }
}
