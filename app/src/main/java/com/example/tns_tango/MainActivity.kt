package com.example.tns_tango

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tns_tango.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Halaman awal SIPEKA Mobile (menu utama setelah login).
 * Sesuai ketentuan: halaman awal TIDAK memakai Toolbar dan tombol back.
 * File: app/src/main/java/com/example/tns_tango/MainActivity.kt
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableSipekaEdgeToEdge()

        session = SessionManager(this)

        // Pengaman: kalau belum login, kembali ke halaman Login
        if (!session.isLoggedIn()) {
            goToLogin()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Header hijau sampai ke belakang status bar, konten tidak tertutup navigation bar
        binding.headerMain.applySystemBarInsets(top = true)
        binding.scrollMain.applySystemBarInsets(bottom = true)

        // Sapaan memakai username yang disimpan di SharedPreferences
        val name = session.getUsername().replaceFirstChar { it.uppercase() }
        binding.tvGreeting.text = getString(R.string.main_hello, name)

        // Tombol 1: Dashboard
        binding.btnMainDashboard.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        // Tombol 2: halaman web terkait topik sawit (WebViewActivity)
        binding.btnMainWebsite.setOnClickListener {
            startActivity(
                WebViewActivity.newIntent(
                    context = this,
                    url = getString(R.string.sipeka_web_url),
                    title = getString(R.string.title_web)
                )
            )
        }

        // Tombol 3 (baru): profil developer
        binding.btnMainProfile.setOnClickListener {
            startActivity(Intent(this, ProfileDeveloperActivity::class.java))
        }

        // Tombol 4: tentang aplikasi
        binding.btnMainAbout.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

        // Tombol 5: keluar (hapus isLogin)
        binding.btnMainLogout.setOnClickListener { showLogoutDialog() }
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.logout_title)
            .setMessage(R.string.logout_message)
            .setNegativeButton(R.string.logout_cancel, null)
            .setPositiveButton(R.string.logout_confirm) { _, _ ->
                session.logout()
                goToLogin()
            }
            .show()
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }
}
