package io.github.kedaitayar.mfm.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.databinding.FragmentMainBinding
import io.github.kedaitayar.mfm.viewmodels.SharedViewModel

@AndroidEntryPoint
class MainFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPagerMain

        viewPager.adapter = MainViewPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()

//        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        setupSnackbarTextObserver()

        binding.fab.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToAddTransactionFragment()
            findNavController().navigate(action)
        }
        return binding.root
    }

    private fun setupSnackbarTextObserver() {
        sharedViewModel.snackbarText.observe(viewLifecycleOwner) {
            it?.getContentIfNotHandled()?.let { text ->
                showSnackBar(text, Snackbar.LENGTH_SHORT)
            }
        }
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            ACCOUNT_PAGE_INDEX -> "Dashboard"
            TRANSACTION_PAGE_INDEX -> "Transaction"
            BUDGET_PAGE_INDEX -> "Budget"
            else -> null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun showSnackBar(text: String, length: Int) {
        Snackbar.make(binding.coordinatorLayout, text, length).show()
    }
}
