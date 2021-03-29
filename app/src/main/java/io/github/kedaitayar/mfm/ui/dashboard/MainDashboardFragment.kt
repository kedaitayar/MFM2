package io.github.kedaitayar.mfm.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentMainDashboardBinding
import io.github.kedaitayar.mfm.ui.dashboard.account.AccountDashboardFragment
import io.github.kedaitayar.mfm.ui.dashboard.transaction_trend_graph.TransactionTrendGraphFragment
import io.github.kedaitayar.mfm.ui.main.MainFragment

@AndroidEntryPoint
class MainDashboardFragment : Fragment(R.layout.fragment_main_dashboard) {
    private var _binding: FragmentMainDashboardBinding? = null
    val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainDashboardBinding.bind(view)

        childFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container_main_account, AccountDashboardFragment())
            replace(R.id.fragment_container_transaction_trend, TransactionTrendGraphFragment())
        }.commit()

        setupHideFABOnScroll()
    }

    private fun setupHideFABOnScroll() {
        if (parentFragment is MainFragment) {
            val parentFragment = (parentFragment as MainFragment)
            binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (scrollY > oldScrollY + 12 && parentFragment.isFABShown()) {
                    parentFragment.hideFAB()
                }
                if (scrollY < oldScrollY - 12 && !parentFragment.isFABShown()) {
                    parentFragment.showFAB()
                }
                if (scrollY == 0) {
                    parentFragment.showFAB()
                }
            })
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}