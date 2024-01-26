package ru.nsu.morozov.qrshare.ui.share

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import ru.nsu.morozov.qrshare.R
import ru.nsu.morozov.qrshare.databinding.FragmentHistoryBinding
import ru.nsu.morozov.qrshare.databinding.FragmentScannerBinding
import java.io.IOException

class ScannerFragment : Fragment(){

    private var _binding: FragmentScannerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var cameraSource: CameraSource;

    @SuppressLint("MissingPermission")
    private fun initCamera(){

        barcodeDetector =
            BarcodeDetector.Builder(context).setBarcodeFormats(Barcode.ALL_FORMATS).build()
        cameraSource =
            CameraSource.Builder(context, barcodeDetector)
                //.setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true)
                //.setFacing(CameraSource.CAMERA_FACING_FRONT)
                .build()
        binding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    cameraSource.start(binding.surfaceView.holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged( holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })



        barcodeDetector.setProcessor(object :Detector.Processor<Barcode>{
            override fun release() {
                //Toast.makeText(context,"barcode scanner has been stopped", Toast.LENGTH_SHORT).show()
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size()!=0){

                    barcodeDetector.release()
                    activity?.runOnUiThread {
                        //Toast.makeText(context, "detection", Toast.LENGTH_SHORT).show()
                        val sid = Uri.parse(barcodes.valueAt(0).displayValue).getQueryParameter("share_id")
                        if (sid!= null) {
                            val args = ShareFragmentArgs.Builder().setShareId(sid).build()
                            view?.findNavController()?.navigate(R.id.navigation_share, args.toBundle())
                        }
                    }
                }
            }

        })

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onResume() {
        super.onResume()
        initCamera()
    }

    override fun onPause() {
        super.onPause()
        cameraSource.release()
    }


}