package com.sevenapps.test

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.sevenapps.test.databinding.ActivityScannerBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ScannerActivity : ComponentActivity(){
    private lateinit var viewBinding    : ActivityScannerBinding
    private lateinit var cameraExecutor : ExecutorService


    private lateinit var imageAnalysis  : ImageAnalysis

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var processCameraProvider: ProcessCameraProvider

    private lateinit var cameraPreview: Preview

    private val cameraSelector  = CameraSelector.DEFAULT_BACK_CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.viewFinder.setOnClickListener{

        }

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(
            {
                processCameraProvider = cameraProviderFuture.get()
                bindCamPreview()
                bindInputAnalyser()
            }, ContextCompat.getMainExecutor(this)
        )




    }

    private fun bindCamPreview(){
        cameraPreview = Preview.Builder()
            .setTargetRotation(viewBinding.viewFinder.display.rotation)
            .build()
        cameraPreview.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)

        processCameraProvider.bindToLifecycle(this,cameraSelector,cameraPreview)
    }

    private fun bindInputAnalyser(){
        val barcodeScanner  : BarcodeScanner = BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build()
        )

        imageAnalysis = ImageAnalysis.Builder()
            .setTargetRotation(viewBinding.viewFinder.display.rotation)
            .build()

        cameraExecutor = Executors.newSingleThreadExecutor()

        imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
            processImageProxy(barcodeScanner, imageProxy)
        }

        processCameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis)
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(barcodeScanner : BarcodeScanner, imageProxy : ImageProxy){
        val inputImage = InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)

        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                if(barcodes.isNotEmpty()){
                    onScan?.invoke(barcodes)
                    onScan = null
                    finish()
                    println("Codigo escaneado!")
                }
            }

            .addOnFailureListener{
                it.printStackTrace()
                Log.e(TAG, it.message ?: it.toString())
            }

            .addOnCompleteListener{
                imageProxy.close()
            }
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
    companion object {
        private val TAG = ScannerActivity::class.simpleName

        private var onScan: ((barcodes: List<Barcode>) -> Unit)? = null
        fun startScanner(context: Context, onScan : (barcodes: List<Barcode>) -> Unit){
            this.onScan = onScan
            Intent(context, ScannerActivity::class.java) .also {
                context.startActivity(it)
            }
        }
    }

}









