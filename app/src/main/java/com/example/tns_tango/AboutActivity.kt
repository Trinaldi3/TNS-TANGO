package com.example.tns_tango

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tns_tango.databinding.ActivityAboutBinding

/**
 * Halaman "Tentang SIPEKA" (pengganti DetailSnackActivity dari project lama).
 * File: app/src/main/java/com/example/tns_tango/AboutActivity.kt
 */
class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableSipekaEdgeToEdge()

        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSipekaToolbar(getString(R.string.title_about))
        binding.rootAbout.applySystemBarInsets(bottom = true)
    }
}
