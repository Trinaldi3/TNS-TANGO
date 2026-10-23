package com.example.tns_tango.feature.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.tns_tango.R
import com.example.tns_tango.core.session.RegistrationData
import com.example.tns_tango.core.session.RegistrationStore
import com.example.tns_tango.core.ui.applyBottomInsetsWithIme
import com.example.tns_tango.core.ui.enableSipekaEdgeToEdge
import com.example.tns_tango.core.ui.setupSipekaToolbar
import com.example.tns_tango.databinding.ActivityRegisterBinding

/**
 * Halaman Registrasi: Nama, Email (otomatis dari halaman sebelumnya, tidak bisa diubah),
 * Username, dan Password.
 * File: app/src/main/java/com/example/tns_tango/feature/auth/RegisterActivity.kt
 *
 * Alur kode:
 * onCreate -> baca EXTRA_EMAIL -> setupEmailField()
 * btnRegister diklik -> attemptRegister() -> AuthValidator.validateRegistration()
 *   - ada error -> pesan tampil di bawah kolom
 *   - valid     -> RegistrationStore.save() -> RegisterSuccessActivity
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var store: RegistrationStore

    /** Ambil email dari Intent, pasang form, lalu sambungkan tombol Registrasi. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableSipekaEdgeToEdge()

        // Halaman ini hanya boleh dibuka dari EmailInputActivity (membawa email)
        val email = intent.getStringExtra(EXTRA_EMAIL).orEmpty()
        if (email.isBlank()) {
            finish()
            return
        }

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSipekaToolbar(getString(R.string.title_register), onBack = { finish() })
        binding.root.applyBottomInsetsWithIme()

        store = RegistrationStore(this)
        setupEmailField(email)
        setupErrorReset()
        binding.btnRegister.setOnClickListener { attemptRegister(email) }
    }

    /** Isi kolom email dan kunci supaya tidak bisa diubah (tidak fokus, tanpa keyboard). */
    private fun setupEmailField(email: String) {
        binding.etEmail.apply {
            setText(email)
            isFocusable = false
            isFocusableInTouchMode = false
            isCursorVisible = false
            keyListener = null
        }
    }

    /** Hapus pesan error di sebuah kolom begitu pengguna mulai mengetik lagi. */
    private fun setupErrorReset() {
        binding.etNama.doAfterTextChanged { binding.tilNama.error = null }
        binding.etUsername.doAfterTextChanged { binding.tilUsername.error = null }
        binding.etPassword.doAfterTextChanged { binding.tilPassword.error = null }
    }

    /**
     * Ambil isi form, validasi, tampilkan error per kolom, dan simpan jika semua valid.
     * @param email email dari halaman sebelumnya (bukan dari ketikan pengguna)
     */
    private fun attemptRegister(email: String) {
        val nama = binding.etNama.text.toString().trim()
        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()

        val errors = AuthValidator.validateRegistration(nama, username, password)
        binding.tilNama.error = errors.nama?.let { getString(it) }
        binding.tilUsername.error = errors.username?.let { getString(it) }
        binding.tilPassword.error = errors.password?.let { getString(it) }
        if (errors.hasError) return

        store.save(RegistrationData(nama, email, username, password))
        startActivity(Intent(this, RegisterSuccessActivity::class.java))
        finish()
    }

    companion object {
        /** Kunci Intent extra untuk email; dipakai pengirim (EmailInputActivity) dan penerima (ini). */
        const val EXTRA_EMAIL = "extra_email"
    }
}
