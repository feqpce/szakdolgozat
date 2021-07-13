package com.example.szakdolgozat.ui.cart

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricPrompt
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.szakdolgozat.databinding.FragmentCartListBinding
import com.example.szakdolgozat.ui.IItemLongClickListener
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import java.util.concurrent.Executor

/**
 * A fragment representing a list of Items.
 */
class CartFragment : Fragment() {
    private val viewModel by viewModels<CartViewModel>()
    private lateinit var binding: FragmentCartListBinding
    private lateinit var scanner: BarcodeScanner

    private val executor: Executor by lazy {
        ContextCompat.getMainExecutor(context)
    }
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpBiometricPrompt()

        setUpBarcodeScanner()

        requestPermissionForCamera()
    }

    private fun setUpBiometricPrompt() {
        biometricPrompt =
            BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    viewModel.checkout()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()

                    Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)

                    Toast.makeText(context, "Authentication error: $errString", Toast.LENGTH_SHORT)
                        .show()

                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Checkout")
            .setSubtitle("Confirm your identity to pay")
            .setNegativeButtonText("Cancel")
            .build()
    }

    private fun setUpBarcodeScanner() {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE
            )
            .build()
        scanner = BarcodeScanning.getClient(options)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCartListBinding.inflate(inflater, container, false)

        val listAdapter = ProductRecyclerViewAdapter(object : IItemLongClickListener<Product> {
            override fun onItemLongClick(item: Product) {
                viewModel.removeProductFromCart(item.id)
            }
        })

        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            list.adapter = listAdapter
            model = viewModel
            checkoutButton.setOnClickListener {
                biometricPrompt.authenticate(promptInfo)
            }
        }

        viewModel.cart.observe(viewLifecycleOwner) {
            Log.d("PRODUCTS", it.toString())
            listAdapter.submitList(it)
            listAdapter.notifyDataSetChanged()
        }
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }

        return binding.root
    }

    private fun requestPermissionForCamera() {
        // Register the permissions callback, which handles the user's response to the
        // system permissions dialog. Save the return value, an instance of
        // ActivityResultLauncher. You can use either a val, as shown in this snippet,
        // or a late init var in your onAttach() or onCreate() method.
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    setupCamera(scanner)
                }
            }
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                setupCamera(scanner)
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    private fun setupCamera(scanner: BarcodeScanner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val executor = ContextCompat.getMainExecutor(requireContext())

        cameraProviderFuture.addListener({
            // Camera provider is now guaranteed to be available
            val cameraProvider = cameraProviderFuture.get()

            // Set up the preview use case to display camera preview.
            val preview = Preview.Builder().build()

            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            val analyzer = QRCodeImageAnalyzer(scanner)
            analyzer.setQRCodeListener(object : QRCodeListener {
                override fun onProductFound(productId: String) {
                    viewModel.addProductToCart(productId)
                }
            })

            imageAnalysis.setAnalyzer(executor, analyzer)

            // Choose the camera by requiring a lens facing
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            // Connect the preview use case to the previewView
            preview.setSurfaceProvider(
                binding.cameraPreview.surfaceProvider
            )

            // Attach use cases to the camera with the same lifecycle owner
            cameraProvider.bindToLifecycle(
                viewLifecycleOwner, cameraSelector, preview, imageAnalysis
            )

        }, executor)
    }
}