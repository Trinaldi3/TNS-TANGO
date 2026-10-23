package com.example.tns_tango.feature.auth

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.example.tns_tango.R
import com.example.tns_tango.core.ui.applyBottomInsetsWithIme
import com.example.tns_tango.core.ui.enableSipekaEdgeToEdge
import com.example.tns_tango.core.ui.setupSipekaToolbar
import com.example.tns_tango.databinding.ActivityEmailInputBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Halaman input email untuk "Register With Gmail". User hanya mengisi email.
 * Dibuka dari tombol Register With Gmail di LoginActivity.
 * File: app/src/main/java/com/example/tns_tango/feature/auth/EmailInputActivity.kt
 *
 * Alur kode:
 * btnLanjut diklik -> submitEmail() -> AuthValidator.validateGmail()
 *   - tidak valid -> showErrorDialog() (MaterialAlertDialog)
 *   - valid       -> openRegister() -> RegisterActivity (email dibawa lewat Intent extra)
 */
class EmailInputActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmailInputBinding

    /** Pasang layout, Toolbar + tombol back, lalu sambungkan tombol Lanjut dan tombol Done keyboard. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableSipekaEdgeToEdge()
        binding = ActivityEmailInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSipekaToolbar(getString(R.string.title_email_input), onBack = { finish() })
        binding.root.applyBottomInsetsWithIme()

        binding.btnLanjut.setOnClickListener { submitEmail() }
        binding.etEmail.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submitEmail()
                true
            } else {
                false
            }
        }
    }

    /** Ambil teks email, validasi, lalu tampilkan dialog error atau lanjut ke Registrasi. */
    private fun submitEmail() {
        val raw = binding.etEmail.text.toString()
        val error = AuthValidator.validateGmail(raw)
        if (error != null) {
            showErrorDialog(error)
        } else {
            openRegister(AuthValidator.normalizeEmail(raw))
        }
    }

    /** Tampilkan alasan email tidak valid di MaterialAlertDialog. */
    private fun showErrorDialog(error: EmailError) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.email_dialog_title)
            .setMessage(error.messageRes)
            .setPositiveButton(R.string.dialog_ok, null)
            .show()
    }

    /** Buka halaman Registrasi dengan email terbawa lewat Intent extra [RegisterActivity.EXTRA_EMAIL]. */
    private fun openRegister(email: String) {
        val intent = Intent(this, RegisterActivity::class.java)
            .putExtra(RegisterActivity.EXTRA_EMAIL, email)
        startActivity(intent)
    }
}
