package ru.nsu.morozov.qrshare.ui.history

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import ru.nsu.morozov.qrshare.R
import ru.nsu.morozov.qrshare.databinding.FragmentSessionInfoBinding
import ru.nsu.morozov.qrshare.ui.AppViewModelProvider
import ru.nsu.morozov.qrshare.ui.share.ShareFragmentArgs
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SessionInfoFragment : Fragment() {

    private var _binding: FragmentSessionInfoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var textViewDatetime: TextView
    private lateinit var editTextName: EditText
    private val sessionInfoViewModel by viewModels<SessionInfoViewModel> { AppViewModelProvider.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentSessionInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        textViewDatetime = binding.datetime
        editTextName = binding.name
        sessionInfoViewModel.sessionInfo.observe(viewLifecycleOwner) {
            if (it != null) {
                editTextName.setText(it.name)
                textViewDatetime.text = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(
                    Date(it.createdAt)
                )
                sessionInfoViewModel.getSharingCount(it.id).observe(viewLifecycleOwner) {count ->
                    if (count == 0) {
                        sessionInfoViewModel.deleteSession(it.id)
                        findNavController().popBackStack()
                    }
                }
            }
        }




        return root
    }

    private val adapter = SharingListAdapter(
        onSend = { sharing ->
            val navController = findNavController()
            activity?.findViewById<BottomNavigationView>(R.id.nav_view)?.let {
                it.selectedItemId = R.id.navigation_share
                var bundle =
                    ShareFragmentArgs.Builder().setShareData(sharing.data).build().toBundle()
                navController.navigate(R.id.navigation_share, bundle)
            }
        },
        onCopy = { sharing ->
            context?.let {
                val clipboardManager = getSystemService(it, ClipboardManager::class.java)
                val clipData = ClipData.newPlainText("shared", sharing.data)
                clipboardManager?.let { manager ->
                    manager.setPrimaryClip(clipData)
                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                }
            }
        },
        onDelete = { sharing ->
            sessionInfoViewModel.deleteSharing(sharing)
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val args = SessionInfoFragmentArgs.fromBundle(it)
            val sid = args.sid
            if (sid != null) {

                val binding = FragmentSessionInfoBinding.bind(view)
                binding.recyclerView.adapter = adapter
                viewLifecycleOwner.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        sessionInfoViewModel.getSharingStream(sid).collect { sharings ->
                            adapter.submitList(sharings)
                        }
                    }
                }
                viewLifecycleOwner.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        sessionInfoViewModel.getSession(sid).collect { session ->
                            session?.let { sessionInfoViewModel.setSessionInfo(session) }
                        }
                    }
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        sessionInfoViewModel.updateSession(binding.name.text.toString())
        _binding = null
    }

}