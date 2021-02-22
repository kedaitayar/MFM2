package io.github.kedaitayar.mfm.ui.account

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.databinding.FragmentMainAccountBinding
import io.github.kedaitayar.mfm.ui.main.MainFragmentDirections
import io.github.kedaitayar.mfm.util.NavigationResult.getNavigationResultLiveData

private const val TAG = "MainAccountFragment"

@AndroidEntryPoint
class MainAccountFragment : Fragment(R.layout.fragment_main_account) {
    private var _binding: FragmentMainAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainAccountBinding.inflate(inflater, container, false)
        context ?: return binding.root

        childFragmentManager.beginTransaction().apply {
            replace(R.id.account_list_fragment_container, AccountListFragment())
        }.commit()

        binding.placeholderButtonAddAccount.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToAddAccountFragment()
            findNavController().navigate(action)
        }

        //get result from AddAccountFragment
        val result = getNavigationResultLiveData<Long>(AddAccountFragment.ADD_ACCOUNT_RESULT_KEY)
        result?.observe(viewLifecycleOwner) {
            Log.i(TAG, "add account result: $it")   //TODO: snackbar to show outcome of account insert
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}