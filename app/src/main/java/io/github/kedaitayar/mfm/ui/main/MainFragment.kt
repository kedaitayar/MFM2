package io.github.kedaitayar.mfm.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.databinding.FragmentMainBinding
import io.github.kedaitayar.mfm.util.EventObserver
import io.github.kedaitayar.mfm.util.exhaustive
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val viewPager = binding.viewPagerMain

        viewPager.adapter = MainViewPagerAdapter(this)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (!isFABShown()) {
                    showFAB()
                }
            }
        })

        TabLayoutMediator(binding.tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()

//        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        setupSnackbarTextObserver()

        binding.fab.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToAddTransactionFragment()
            findNavController().navigate(action)
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

        return binding.root
    }

//    private fun setupAddEditAccountFragmentResultListener() {
//        setFragmentResultListener(AddEditAccountFragment.NAVIGATE_BACK_EDIT_RESULT) { _: String, bundle: Bundle ->
//            val result = bundle.getInt(AddEditAccountFragment.RESULT_KEY)
//        }
//    }

    fun hideFAB() {
        binding.fab.hide()
    }

    fun showFAB() {
        binding.fab.show()
    }

    fun isFABShown(): Boolean = binding.fab.isShown

    private fun setupSnackbarTextObserver() {
        mainViewModel.snackbarText.observe(viewLifecycleOwner, EventObserver{
            showSnackBar(it, Snackbar.LENGTH_SHORT)
        })
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            DASHBOARD_PAGE_INDEX -> "Dashboard"
            TRANSACTION_PAGE_INDEX -> "Transaction"
            BUDGET_PAGE_INDEX -> "Budget"
            else -> null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showSnackBar(text: String, length: Int) {
        Snackbar.make(binding.coordinatorLayout, text, length).show()
    }
}
