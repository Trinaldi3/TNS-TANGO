package com.example.tns_tango.feature.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tns_tango.R
import com.example.tns_tango.core.session.SessionManager
import com.example.tns_tango.core.ui.applySystemBarInsets
import com.example.tns_tango.core.ui.enableSipekaEdgeToEdge
import com.example.tns_tango.core.ui.setupSipekaToolbar
import com.example.tns_tango.feature.main.MainActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * Halaman Login (UC01).
 * File: app/src/main/java/com/example/tns_tango/feature/auth/LoginActivity.kt
 *
 * Alur kode:
 * btnLogin diklik -> attemptLogin() -> validateInput() -> isCredentialValid()
 *   - benar -> onLoginSuccess(): SessionManager.saveLogin() (isLogin = true) -> MainActivity (tab Dashboard)
 *   - salah -> onLoginFailed(): pesan error
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var tilUsername: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var session: SessionManager

    /** Pasang layout, Toolbar, lalu sambungkan tombol Masuk ke attemptLogin(). */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableSipekaEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Toolbar + tombol back (menutup halaman Login)
        setupSipekaToolbar(getString(R.string.title_login), onBack = { finish() })
        findViewById<View>(R.id.rootLogin).applySystemBarInsets(bottom = true)

        session = SessionManager(this)
        tilUsername = findViewById(R.id.tilUsername)
        tilPassword = findViewById(R.id.tilPassword)
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener { attemptLogin() }
    }

    /** Langkah 1: ambil teks dari form, lalu validasi dan cek kredensial. */
    private fun attemptLogin() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (!validateInput(username, password)) return

        if (isCredentialValid(username, password)) {
            onLoginSuccess()
        } else {
            onLoginFailed()
        }
    }

    /** Langkah 2: input tidak boleh kosong. Pesan error tampil di bawah kolom yang kosong. */
    private fun validateInput(username: String, password: String): Boolean {
        tilUsername.error =
            if (username.isEmpty()) getString(R.string.sipeka_err_username_kosong) else null
        tilPassword.error =
            if (password.isEmpty()) getString(R.string.sipeka_err_password_kosong) else null
        return username.isNotEmpty() && password.isNotEmpty()
    }

    /**
     * Langkah 3: cek username dan password.
     * Username: huruf besar/kecil diabaikan. Password: harus sama persis.
     * TODO: ganti dengan request ke API Laravel (Retrofit) di pertemuan berikutnya.
     */
    private fun isCredentialValid(username: String, password: String): Boolean {
        return username.equals(VALID_USERNAME, ignoreCase = true) && password == VALID_PASSWORD
    }

    /** Langkah 4a: berhasil -> simpan isLogin = true lalu buka MainActivity (langsung tab Dashboard). */
    private fun onLoginSuccess() {
        session.saveLogin(VALID_USERNAME)
        Toast.makeText(this, R.string.sipeka_login_berhasil, Toast.LENGTH_SHORT).show()

        val intent = Intent(this, MainActivity::class.java).apply {
            // Kosongkan tumpukan halaman supaya tombol back tidak kembali ke Login
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    /** Langkah 4b: gagal -> tampilkan pesan, isLogin tidak disimpan. */
    private fun onLoginFailed() {
        tilPassword.error = getString(R.string.sipeka_err_login_gagal)
        Toast.makeText(this, R.string.sipeka_err_login_gagal, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val VALID_USERNAME = "admin"
        private const val VALID_PASSWORD = "admin123"
    }
}
