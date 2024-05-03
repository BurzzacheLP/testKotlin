package com.sevenapps.test

import android.Manifest
import android.content.ContentValues
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
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.sevenapps.test.databinding.ActivityHomeBinding
import com.sevenapps.test.databinding.ActivityScannerBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : ComponentActivity(){
    private lateinit var viewBinding    : ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(viewBinding.root)

        requestPermissions()

        // Abrir scanner

        viewBinding.homeBtnScan.setOnClickListener{
            val intent = Intent(this, ScannerActivity::class.java)
            startActivity(intent)

            startScanner()
        }

    }

    private fun startScanner(){
        ScannerActivity.startScanner(this) {barcodes ->
            barcodes.forEach {barcode ->
                when(barcode.valueType){
                    Barcode.TYPE_URL ->{

                    }

                    Barcode.TYPE_PRODUCT ->{

                    }
                    else -> {

                    }
                }
            }
        }
    }

    //--- PERMISOS ---//
    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    // TOdo decirle al usuario que necesitamos si o si los permisos de camara
    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            var permissionGranted = true

            permissions.entries.forEach{
                if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
            }

            if (!permissionGranted) {
                Toast.makeText(baseContext,
                    "Permission Request Denied :(",
                    Toast.LENGTH_SHORT).show()
            } else {
                //tartCamera()
            }
        }


    companion object {
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }


}









