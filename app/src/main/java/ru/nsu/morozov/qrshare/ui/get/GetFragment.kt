package ru.nsu.morozov.qrshare.ui.get

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.squareup.picasso.Picasso
import ru.nsu.morozov.qrshare.MainActivity
import ru.nsu.morozov.qrshare.R
import ru.nsu.morozov.qrshare.databinding.FragmentGetBinding
import ru.nsu.morozov.qrshare.ui.AppViewModelProvider


class GetFragment : Fragment() {

    private var _binding: FragmentGetBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val getViewModel by viewModels<GetViewModel> { AppViewModelProvider.Factory }

    private var dialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGetBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val sharedView: TextView = binding.sharingData
        getViewModel.data.observe(viewLifecycleOwner) {
            sharedView.text = it
        }

        val qrView: ImageView = binding.qrView
        getViewModel.sid.observe(viewLifecycleOwner) {
            Picasso.get().load("${resources.getString(R.string.server_url)}/static/$it.png")
                .into(qrView)
        }

        qrView.setOnClickListener {
            if (getViewModel.connected()) {
                qrView.visibility = View.GONE
                dialog = Dialog(requireContext())
                dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                val dialogView = layoutInflater.inflate(R.layout.dialog_latyout, null)
                dialog!!.setContentView(dialogView)

                val overlayImageView: ImageView = dialog!!.findViewById(R.id.qr_view)
                Picasso.get()
                    .load("${resources.getString(R.string.server_url)}/static/${getViewModel.sid.value}.png")
                    .into(overlayImageView)
                dialog!!.setOnDismissListener {
                    qrView.visibility = View.VISIBLE
                }
                dialog!!.show()
            }
        }
        binding.copy.setOnClickListener {
            context?.let {
                val clipboardManager =
                    ContextCompat.getSystemService(it, ClipboardManager::class.java)
                val clipData = ClipData.newPlainText("shared", binding.sharingData.text.toString())
                clipboardManager?.let { manager ->
                    manager.setPrimaryClip(clipData)
                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.connect.setOnClickListener {
            binding.connect.visibility = View.GONE
            binding.qrView.visibility = View.VISIBLE
            getViewModel.startWebSocketConnection(
                url = resources.getString(R.string.server_url),
                onConnected = { res ->
                    requireActivity().runOnUiThread {
                        if (!res) {
                            binding.connect.visibility = View.VISIBLE
                            binding.qrView.visibility = View.GONE
                            binding.qrView.setImageResource(android.R.color.transparent)

                            dialog?.dismiss()
                            var errDialog = Dialog(requireContext())
                            errDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                            errDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                            val dialogView = layoutInflater.inflate(R.layout.error_latyout, null)
                            errDialog!!.setContentView(dialogView)
                            errDialog!!.show()
                        }
                    }
                },
                onShareId = { sid ->
                    requireActivity().runOnUiThread {
                        getViewModel.setSid(sid)
                    }
                },
                onShared = { data ->
                    requireActivity().runOnUiThread {
                        getViewModel.setData(data)

                        getViewModel.sid.value?.let {
                            getViewModel.saveSession(it)
                            getViewModel.saveSharing(it, data)
                        }

                        val bottomNavView = (requireActivity() as MainActivity).getBottomNavView()
                        val menu = bottomNavView.menu
                        val menuItem = menu.findItem(R.id.navigation_get)
                        if (!menuItem.isChecked) {
                            menuItem?.setTitle(R.string.title_get_new)
                        }

                        _binding?.let {
                            it.buttons.visibility = View.VISIBLE
                        }

                    }
                },
                onClosed = {
                    requireActivity().runOnUiThread {
                        _binding?.let {
                            it.connect.visibility = View.VISIBLE
                            it.qrView.visibility = View.GONE
                            it.qrView.setImageResource(android.R.color.transparent)
                        }
                        dialog?.dismiss()
                    }
                }
            )
        }

        if (getViewModel.connected()) {
            binding.connect.visibility = View.GONE
            binding.qrView.visibility = View.VISIBLE
        }
        else{
            binding.connect.visibility = View.VISIBLE
            binding.qrView.visibility = View.GONE
            binding.qrView.setImageResource(android.R.color.transparent)
            dialog?.dismiss()
        }

        if (getViewModel.shared()) {
            binding.buttons.visibility = View.VISIBLE
        }
        else{
            binding.buttons.visibility = View.GONE
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        val bottomNavView = (requireActivity() as MainActivity).getBottomNavView()
        val menu = bottomNavView.menu
        val menuItem = menu?.findItem(R.id.navigation_get)
        menuItem?.setTitle(R.string.title_get)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}