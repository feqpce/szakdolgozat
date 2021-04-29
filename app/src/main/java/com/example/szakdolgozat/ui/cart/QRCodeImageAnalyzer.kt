package com.example.szakdolgozat.ui.cart

import android.annotation.SuppressLint
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.common.InputImage

class QRCodeImageAnalyzer(val scanner: BarcodeScanner) : ImageAnalysis.Analyzer {
    var listener: QRCodeListener? = null

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            processImage(imageProxy, mediaImage, image)
        }
    }

    private fun processImage(imageProxy: ImageProxy, mediaImage: Image, inputImage: InputImage) {
        scanner.process(inputImage).addOnSuccessListener { barcodes ->
            for (barcode in barcodes) {
                barcode.rawValue?.let {
                    Log.d("BARCODE", it)
                    listener?.onProductFound(it)
                }
            }
        }.addOnFailureListener {
            Log.d("BARCODE", "Hiba: ${it.message}")
        }.addOnCompleteListener {
            mediaImage.close()
            imageProxy.close()
        }
    }

    fun setQRCodeListener(listener: QRCodeListener) {
        this.listener = listener
    }
}

interface QRCodeListener {
    fun onProductFound(productId: String)
}