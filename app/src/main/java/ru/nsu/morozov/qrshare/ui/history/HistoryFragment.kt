package ru.nsu.morozov.qrshare.ui.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import ru.nsu.morozov.qrshare.R
import ru.nsu.morozov.qrshare.databinding.FragmentHistoryBinding
import ru.nsu.morozov.qrshare.ui.AppViewModelProvider

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val historyViewModel by viewModels<HistoryViewModel> { AppViewModelProvider.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root

    }

    private val adapter = SessionListAdapter(
        onEdit = { session ->
            //Toast.makeText(context, session.name, Toast.LENGTH_SHORT).show()
            var bundle = SessionInfoFragmentArgs.Builder(session.id).build().toBundle()
            findNavController().navigate(R.id.fragment_session_info, bundle)
        },
        onDelete = { session ->
            historyViewModel.deleteSession(session)
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHistoryBinding.bind(view)
        binding.recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                historyViewModel.sessionStream.collect {
                    adapter.submitList(it)
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}