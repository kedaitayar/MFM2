package io.github.kedaitayar.mfm.ui.transaction.quick_transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.data.entity.QuickTransaction
import io.github.kedaitayar.mfm.databinding.FragmentQuickTransactionSelectionDialogBinding
import io.github.kedaitayar.mfm.ui.main.MainFragmentDirections
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class QuickTransactionSelectionDialogFragment : BottomSheetDialogFragment() {
    private val quickTransactionSelectionViewModel: QuickTransactionSelectionViewModel by viewModels()
    private var _binding: FragmentQuickTransactionSelectionDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentQuickTransactionSelectionDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val quickTransactionAdapter = QuickTransactionSelectionAdapter()
        binding.apply {
            recyclerViewQuickTransaction.adapter = quickTransactionAdapter
            recyclerViewQuickTransaction.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewQuickTransaction.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                quickTransactionSelectionViewModel.allQuickTransaction.collect {
                    quickTransactionAdapter.submitList(it)
                }
            }
        }

        quickTransactionAdapter.setOnItemClickListener(object :
            QuickTransactionSelectionAdapter.OnItemClickListener {
            override fun onClick(item: QuickTransaction) {
                val action =
                    QuickTransactionSelectionDialogFragmentDirections.actionQuickTransactionSelectionDialogFragmentToAddTransactionFragment(
                        item
                    )
                findNavController().navigate(action)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}