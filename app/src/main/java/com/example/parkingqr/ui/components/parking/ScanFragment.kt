package com.example.parkingqr.ui.components.parking

import android.content.pm.PackageManager
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.animation.TranslateAnimation
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentScanBinding
import com.example.parkingqr.ui.base.BaseFragment
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.coroutines.*
import java.io.IOException


class ScanFragment : BaseFragment() {

    private lateinit var binding: FragmentScanBinding
    private lateinit var cameraView: SurfaceView
    private lateinit var cameraSource: CameraSource
    private lateinit var surfaceHolder: SurfaceHolder
    private lateinit var barcode: BarcodeDetector
    private lateinit var scanLine: View
    private val parkingViewModel: ParkingViewModel by hiltNavGraphViewModels(R.id.parkingFragment)


    override fun observeViewModel() {

    }

    override fun initViewBinding(): View {
        binding = FragmentScanBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initListener() {
        showActionBar(getString(R.string.scan_fragment_name))
        scanLine = binding.scanLine
        startScanLineAnimation()

        cameraView = binding.sfv
        cameraView.setZOrderMediaOverlay(true)
        surfaceHolder = cameraView.holder
        barcode = BarcodeDetector.Builder(requireContext()).setBarcodeFormats(Barcode.QR_CODE).build()

        if (!barcode.isOperational) {
            showMessage("Không thể mở màn hình quét")
            getNavController().popBackStack()
        }
        cameraSource =
            CameraSource.Builder(requireContext(), barcode).setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(24F).setRequestedPreviewSize(1920, 1024).build()
        cameraView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            android.Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraSource.start(cameraView.holder)
                    }
                    else{
                        getNavController().popBackStack()
                    }
                } catch (e: IOException) {
                    Log.e("Error Scan", e.message.toString())
                    getNavController().popBackStack()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop();
            }

        })
        barcode.setProcessor(object : Detector.Processor<Barcode>{
            override fun release() {}

            override fun receiveDetections(p0: Detector.Detections<Barcode>) {
                val barcodes: SparseArray<Barcode> = p0.detectedItems
                if(barcodes.size() > 0){
                    handleReceiveQRCode(barcodes)
                }
            }

        })
    }
    private fun handleReceiveQRCode(barcodes: SparseArray<Barcode>){
        CoroutineScope(Dispatchers.Main).launch {
            Log.d("QR CODE", barcodes.valueAt(0).displayValue.toString())
            parkingViewModel.getDataFromQRCode(barcodes.valueAt(0).displayValue)
            barcode.release();
            getNavController().popBackStack()
        }
    }

    private fun startScanLineAnimation() {
        val screenHeight = resources.displayMetrics.heightPixels
        val animation = TranslateAnimation(0f, 0f, 0f, screenHeight.toFloat())
        animation.duration = 3500
        animation.repeatCount = TranslateAnimation.INFINITE
        scanLine.startAnimation(animation)
    }
}