package com.example.tns_tango.feature.website

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.tns_tango.R
import com.example.tns_tango.core.ui.applySystemBarInsets
import com.example.tns_tango.databinding.FragmentWebsiteBinding

/**
 * Tab 3 "Website": menampilkan halaman web topik sawit di dalam aplikasi
 * (pengganti WebViewActivity dari project sebelumnya).
 * File: app/src/main/java/com/example/tns_tango/feature/website/WebsiteFragment.kt
 *
 * Alur kode:
 * onViewCreated -> setupWebView() -> loadUrl(sipeka_web_url)
 *   gagal memuat -> onReceivedError -> showError(true) -> tombol "Coba lagi" -> reload()
 * Tombol back sistem: MainActivity memanggil goBackInPage() lebih dulu.
 */
class WebsiteFragment : Fragment() {

    private var _binding: FragmentWebsiteBinding? = null
    private val binding get() = _binding!!

    private var hasError = false
    private var lastUrl: String = ""

    /** Buat tampilan dari fragment_website.xml. */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWebsiteBinding.inflate(inflater, container, false)
        return binding.root
    }

    /** Pasang header, konfigurasi WebView, tombol coba lagi, lalu muat halaman web. */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.pageHeader.tvPageTitle.setText(R.string.title_web)
        binding.pageHeader.root.applySystemBarInsets(top = true)

        lastUrl = getString(R.string.sipeka_web_url)
        setupWebView()

        binding.btnRetry.setOnClickListener { reload() }

        // Pulihkan riwayat halaman setelah layar diputar; kalau tidak ada, muat halaman awal
        val restored = savedInstanceState?.let { binding.webView.restoreState(it) }
        if (restored == null) binding.webView.loadUrl(lastUrl)
    }

    /** Konfigurasi WebView: JavaScript, zoom, indikator loading, dan penanganan error. */
    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        binding.webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
        }

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                hasError = false
                if (!url.isNullOrEmpty()) lastUrl = url
                _binding?.progressBar?.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                _binding?.progressBar?.visibility = View.GONE
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

        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                val b = _binding ?: return
                b.progressBar.progress = newProgress
                b.progressBar.visibility = if (newProgress in 1..99) View.VISIBLE else View.GONE
            }
        }
    }

    /** Muat ulang halaman terakhir (tombol "Coba lagi"). */
    private fun reload() {
        hasError = false
        showError(false)
        binding.webView.loadUrl(lastUrl)
    }

    /** Tampilkan layar error (true) atau halaman web (false). */
    private fun showError(show: Boolean) {
        val b = _binding ?: return
        b.layoutError.visibility = if (show) View.VISIBLE else View.GONE
        b.webView.visibility = if (show) View.INVISIBLE else View.VISIBLE
    }

    /**
     * Dipanggil MainActivity saat tombol back sistem ditekan.
     * @return true jika halaman web mundur satu langkah; false jika sudah di halaman pertama.
     */
    fun goBackInPage(): Boolean {
        val b = _binding ?: return false
        return if (b.webView.canGoBack()) {
            b.webView.goBack()
            true
        } else {
            false
        }
    }

    /** Simpan riwayat halaman WebView saat layar diputar. */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        _binding?.webView?.saveState(outState)
    }

    /** Hentikan dan hancurkan WebView, lalu lepas binding. */
    override fun onDestroyView() {
        _binding?.webView?.apply {
            stopLoading()
            destroy()
        }
        _binding = null
        super.onDestroyView()
    }
}
