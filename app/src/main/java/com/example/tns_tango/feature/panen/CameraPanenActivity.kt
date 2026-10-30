package com.example.tns_tango.feature.panen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.tns_tango.R
import com.example.tns_tango.databinding.ActivityCameraPanenBinding
import java.io.File

/** Layar kamera khusus untuk mengambil satu foto dokumentasi kegiatan panen. */
class CameraPanenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraPanenBinding
    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startCamera()
            } else {
                Toast.makeText(this, R.string.panen_foto_izin_ditolak, Toast.LENGTH_LONG).show()
                setResult(RESULT_CANCELED)
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraPanenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCapturePhoto.setOnClickListener { takePhoto() }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    /** Menyiapkan preview kamera belakang dan use case pengambilan gambar. */
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            if (isFinishing || isDestroyed) return@addListener
            try {
                val provider = cameraProviderFuture.get()
                cameraProvider = provider

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

                val captureUseCase = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()
                imageCapture = captureUseCase

                provider.unbindAll()
                provider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    captureUseCase
                )
            } catch (exception: Exception) {
                Toast.makeText(this, R.string.panen_foto_gagal, Toast.LENGTH_LONG).show()
                setResult(RESULT_CANCELED)
                finish()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    /** Mengambil foto dan mengembalikan path file ke InputPanenActivity. */
    private fun takePhoto() {
        val capture = imageCapture
        if (capture == null) {
            Toast.makeText(this, R.string.panen_foto_gagal, Toast.LENGTH_SHORT).show()
            return
        }

        val photoDirectory = File(filesDir, PHOTO_DIRECTORY).apply { mkdirs() }
        val photoFile = File(photoDirectory, "foto_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        binding.btnCapturePhoto.isEnabled = false
        capture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val result = Intent().putExtra(EXTRA_FOTO_PATH, photoFile.absolutePath)
                    setResult(RESULT_OK, result)
                    finish()
                }

                override fun onError(exception: ImageCaptureException) {
                    photoFile.delete()
                    binding.btnCapturePhoto.isEnabled = true
                    Toast.makeText(
                        this@CameraPanenActivity,
                        R.string.panen_foto_gagal,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }

    override fun onDestroy() {
        cameraProvider?.unbindAll()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_FOTO_PATH = "com.example.tns_tango.extra.FOTO_PANEN_PATH"
        private const val PHOTO_DIRECTORY = "foto_panen"
    }
}
