package ru.nsu.morozov.qrshare.ui.share

import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import ru.nsu.morozov.qrshare.R
import ru.nsu.morozov.qrshare.data.Session
import ru.nsu.morozov.qrshare.databinding.FragmentShareBinding
import ru.nsu.morozov.qrshare.ui.AppViewModelProvider
import java.io.IOException

class ShareFragment : Fragment(), EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {

    private var _binding: FragmentShareBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var textViewScan: TextView
    private lateinit var editTextInput: EditText
    private val shareViewModel by activityViewModels<ShareViewModel> { AppViewModelProvider.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentShareBinding.inflate(inflater, container, false)
        val root: View = binding.root

        textViewScan = binding.textViewScan
        shareViewModel.shareId.observe(viewLifecycleOwner) {
            textViewScan.text = it
        }

        editTextInput = binding.editTextInput
        shareViewModel.shareData.observe(viewLifecycleOwner) {
            editTextInput.setText(it)
        }

        shareViewModel.scanned.observe(viewLifecycleOwner) {

            binding.buttonSend.isEnabled = it
        }

        binding.buttonScan.setOnClickListener {
            cameraTask(it)
        }
        binding.buttonSend.setOnClickListener {
            binding.buttonSend.isEnabled = false
            postData(
                textViewScan.text.toString(),
                editTextInput.text.toString(),
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        activity!!.runOnUiThread {
                            Toast.makeText(context, "server error", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        activity!!.runOnUiThread {
                            //Toast.makeText(context, "success", Toast.LENGTH_SHORT).show()
                            shareViewModel.saveSession(binding.textViewScan.text.toString())
                            shareViewModel.saveSharing(
                                binding.textViewScan.text.toString(),
                                editTextInput.text.toString()
                            )

                            binding.editTextInput.setText("")
                            binding.buttonSend.isEnabled = true
                        }
                    }
                })
        }

        return root
    }

    private fun postData(sid: String, data: String, callback: Callback) {
        val client = OkHttpClient()

        val body = FormBody.Builder()
            .add("share_id", sid)
            .add("text", data)
            .build()
        val request = Request.Builder()
            .url("${resources.getString(R.string.server_url)}${resources.getString(R.string.share_path)}")
            .post(body)
            .build()

        client.newCall(request).enqueue(callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val args = ShareFragmentArgs.fromBundle(it)
            args.shareId?.let { sid ->
                shareViewModel.setShareId(sid)
            }
            args.shareData?.let { data ->
                shareViewModel.setShareData(data)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        shareViewModel.setShareData(editTextInput.text.toString())

        _binding = null
    }


    private fun hasCameraAccess(): Boolean {
        return EasyPermissions.hasPermissions(requireContext(), android.Manifest.permission.CAMERA)
    }

    private fun cameraTask(view: View) {

        if (hasCameraAccess()) {
            view.findNavController().navigate(R.id.action_navigation_share_to_navigation_scanner)
        } else {
            EasyPermissions.requestPermissions(
                this,
                "This app needs access to your camera so you can take pictures.",
                123,
                android.Manifest.permission.CAMERA
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onRationaleDenied(requestCode: Int) {
    }

    override fun onRationaleAccepted(requestCode: Int) {
    }

}