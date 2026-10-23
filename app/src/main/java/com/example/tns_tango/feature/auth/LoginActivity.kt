package com.example.tns_tango.feature.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tns_tango.R
import com.example.tns_tango.core.session.RegistrationStore
import com.example.tns_tango.core.session.SessionManager
import com.example.tns_tango.core.ui.applySystemBarInsets
import com.example.tns_tango.core.ui.enableSipekaEdgeToEdge
import com.example.tns_tango.core.ui.setupSipekaToolbar
import com.example.tns_tango.feature.main.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * Halaman Login (UC01) dengan tombol Register With Gmail.
 * File: app/src/main/java/com/example/tns_tango/feature/auth/LoginActivity.kt
 *
 * Alur kode:
 * btnLogin diklik -> attemptLogin() -> validateInput() -> isCredentialValid()
 *   - benar -> onLoginSuccess(): SessionManager.saveLogin() (isLogin = true) -> MainActivity (Home)
 *   - salah -> onLoginFailed(): MaterialAlertDialog
 * btnRegisterGmail diklik -> EmailInputActivity (lihat EmailInputActivity.kt)
 *
 * Aturan login: boleh masuk jika salah satu terpenuhi:
 * 1. username == password
 * 2. username dan password sama dengan data registrasi di SharedPreferences
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var tilUsername: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegisterGmail: Button
    private lateinit var session: SessionManager
    private lateinit var registrationStore: RegistrationStore

    /** Pasang layout dan Toolbar, sambungkan tombol Masuk dan Register With Gmail, isi username jika dikirim. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableSipekaEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Toolbar + tombol back (menutup halaman Login)
        setupSipekaToolbar(getString(R.string.title_login), onBack = { finish() })
        findViewById<View>(R.id.rootLogin).applySystemBarInsets(bottom = true)

        session = SessionManager(this)
        registrationStore = RegistrationStore(this)
        tilUsername = findViewById(R.id.tilUsername)
        tilPassword = findViewById(R.id.tilPassword)
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegisterGmail = findViewById(R.id.btnRegisterGmail)

        // Setelah registrasi berhasil, username terisi otomatis
        intent.getStringExtra(EXTRA_USERNAME)?.let { etUsername.setText(it) }

        btnLogin.setOnClickListener { attemptLogin() }
        btnRegisterGmail.setOnClickListener { openRegisterWithGmail() }
    }

    /** Buka halaman input email (langkah pertama Register With Gmail). */
    private fun openRegisterWithGmail() {
        startActivity(Intent(this, EmailInputActivity::class.java))
    }

    /** Langkah 1: ambil teks dari form, lalu validasi dan cek kredensial. */
    private fun attemptLogin() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (!validateInput(username, password)) return

        if (isCredentialValid(username, password)) {
            onLoginSuccess(username)
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
     * Langkah 3: cek login. Lolos jika salah satu kondisi terpenuhi:
     * 1. username sama dengan password
     * 2. username dan password cocok dengan data registrasi di SharedPreferences
     */
    private fun isCredentialValid(username: String, password: String): Boolean {
        val sameAsPassword = username == password
        val matchesRegistration = registrationStore.isMatch(username, password)
        return sameAsPassword || matchesRegistration
    }

    /**
     * Langkah 4a: berhasil -> simpan isLogin = true lalu buka MainActivity (Home).
     * @param username username yang dipakai login (tampil di sapaan Beranda)
     */
    private fun onLoginSuccess(username: String) {
        session.saveLogin(username)
        Toast.makeText(this, R.string.sipeka_login_berhasil, Toast.LENGTH_SHORT).show()

        val intent = Intent(this, MainActivity::class.java).apply {
            // Kosongkan tumpukan halaman supaya tombol back tidak kembali ke Login
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    /** Langkah 4b: gagal -> tampilkan MaterialAlertDialog, isLogin tidak disimpan. */
    private fun onLoginFailed() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.login_dialog_title)
            .setMessage(R.string.sipeka_err_login_gagal)
            .setPositiveButton(R.string.dialog_ok, null)
            .show()
    }

    companion object {
        /** Kunci Intent extra: username yang diisi otomatis setelah registrasi. */
        const val EXTRA_USERNAME = "extra_username"
    }
}
