package io.github.kedaitayar.mfm.ui.transaction.quick_transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.data.entity.QuickTransaction
import io.github.kedaitayar.mfm.databinding.FragmentQuickTransactionSelectionDialogBinding
import io.github.kedaitayar.mfm.util.safeCollection

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

        quickTransactionSelectionViewModel.allQuickTransaction.safeCollection(viewLifecycleOwner) {
            quickTransactionAdapter.submitList(it)
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