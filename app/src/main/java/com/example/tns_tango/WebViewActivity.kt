package com.example.tns_tango

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.tns_tango.databinding.ActivityWebViewBinding

/**
 * Menampilkan halaman web terkait topik (sawit) di dalam aplikasi.
 * File: app/src/main/java/com/example/tns_tango/WebViewActivity.kt
 *
 * Langkah sesuai tugas:
 * 1. WebView ada di activity_web_view.xml
 * 2. Konfigurasi WebView di setupWebView()
 * 3. Izin INTERNET ada di AndroidManifest.xml
 * 4. Tombol back sistem -> halaman web sebelumnya (setupBackNavigation)
 * 5. Panah back di Toolbar -> tutup WebView, kembali ke Activity sebelumnya
 */
class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebViewBinding
    private var hasError = false
    private var lastUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableSipekaEdgeToEdge()

        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val startUrl = intent.getStringExtra(EXTRA_URL) ?: getString(R.string.sipeka_web_url)
        val pageTitle = intent.getStringExtra(EXTRA_TITLE) ?: getString(R.string.title_web)
        lastUrl = startUrl

        // Toolbar + tombol back: panah di Toolbar menutup WebView (kembali ke Activity sebelumnya)
        setupSipekaToolbar(pageTitle) { finish() }
        binding.rootWebView.applySystemBarInsets(bottom = true)

        setupWebView()
        setupBackNavigation()

        binding.btnRetry.setOnClickListener {
            hasError = false
            showError(false)
            binding.webView.loadUrl(lastUrl)
        }

        if (savedInstanceState == null) {
            binding.webView.loadUrl(startUrl)
        } else {
            binding.webView.restoreState(savedInstanceState)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val webView = binding.webView
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                hasError = false
                if (!url.isNullOrEmpty()) lastUrl = url
                binding.progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.progressBar.visibility = View.GONE
                showError(hasError)
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                if (request.isForMainFrame) {
                    hasError = true
                    showError(true)
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                binding.progressBar.progress = newProgress
                binding.progressBar.visibility =
                    if (newProgress in 1..99) View.VISIBLE else View.GONE
            }
        }
    }

    /** Tombol back sistem: mundur ke halaman web sebelumnya; kalau sudah habis, tutup Activity. */
    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun showError(show: Boolean) {
        binding.layoutError.visibility = if (show) View.VISIBLE else View.GONE
        binding.webView.visibility = if (show) View.INVISIBLE else View.VISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.webView.saveState(outState)
    }

    override fun onDestroy() {
        binding.webView.apply {
            stopLoading()
            destroy()
        }
        super.onDestroy()
    }

    companion object {
        private const val EXTRA_URL = "extra_url"
        private const val EXTRA_TITLE = "extra_title"

        fun newIntent(context: Context, url: String, title: String): Intent =
            Intent(context, WebViewActivity::class.java)
                .putExtra(EXTRA_URL, url)
                .putExtra(EXTRA_TITLE, title)
    }
}
