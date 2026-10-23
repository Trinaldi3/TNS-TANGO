package com.example.tns_tango.feature.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.tns_tango.R
import com.example.tns_tango.core.session.RegistrationStore
import com.example.tns_tango.core.ui.applySystemBarInsets
import com.example.tns_tango.core.ui.enableSipekaEdgeToEdge
import com.example.tns_tango.core.ui.setupSipekaToolbar
import com.example.tns_tango.databinding.ActivityRegisterSuccessBinding

/**
 * Halaman informasi "Registrasi Berhasil": ringkasan data yang tersimpan.
 * Password sengaja tidak ditampilkan.
 * File: app/src/main/java/com/example/tns_tango/feature/auth/RegisterSuccessActivity.kt
 *
 * Alur kode:
 * onCreate -> RegistrationStore.load() -> showSummary()
 * btnKeLogin / tombol back -> goToLogin() -> LoginActivity (username terisi otomatis)
 */
class RegisterSuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterSuccessBinding

    /** Muat data registrasi, tampilkan ringkasan, dan arahkan semua jalur kembali ke Login. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableSipekaEdgeToEdge()

        val data = RegistrationStore(this).load()
        if (data == null) {
            goToLogin(null)
            return
        }

        binding = ActivityRegisterSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSipekaToolbar(getString(R.string.title_register_success), onBack = { goToLogin(data.username) })
        binding.root.applySystemBarInsets(bottom = true)

        binding.tvNama.text = data.nama
        binding.tvEmail.text = data.email
        binding.tvUsername.text = data.username

        binding.btnKeLogin.setOnClickListener { goToLogin(data.username) }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            // Tombol back sistem juga kembali ke Login (bukan ke form registrasi)
            override fun handleOnBackPressed() = goToLogin(data.username)
        })
    }

    /**
     * Buka LoginActivity dan kosongkan tumpukan halaman registrasi.
     * @param username username yang akan terisi otomatis di kolom login (boleh null)
     */
    private fun goToLogin(username: String?) {
        val intent = Intent(this, LoginActivity::class.java).apply {
            username?.let { putExtra(LoginActivity.EXTRA_USERNAME, it) }
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }
}
