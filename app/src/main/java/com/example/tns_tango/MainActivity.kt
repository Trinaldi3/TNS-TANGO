package com.example.tns_tango

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tns_tango.databinding.ActivityMainBinding

/**
 * Halaman awal SIPEKA Mobile.
 * Sesuai ketentuan: halaman awal TIDAK memakai Toolbar dan tombol back.
 * File: app/src/main/java/com/example/tns_tango/MainActivity.kt
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableSipekaEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Header hijau sampai ke belakang status bar, konten tidak tertutup navigation bar
        binding.headerMain.applySystemBarInsets(top = true)
        binding.scrollMain.applySystemBarInsets(bottom = true)

        // Tombol 1: ke halaman Login
        binding.btnMainLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Tombol 2 (baru): buka halaman web terkait topik sawit lewat WebViewActivity
        binding.btnMainWebsite.setOnClickListener {
            startActivity(
                WebViewActivity.newIntent(
                    context = this,
                    url = getString(R.string.sipeka_web_url),
                    title = getString(R.string.title_web)
                )
            )
        }

        // Tombol 3: tentang aplikasi
        binding.btnMainAbout.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }
}
