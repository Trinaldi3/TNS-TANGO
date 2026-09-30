package com.example.tns_tango

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val tilUsername = findViewById<TextInputLayout>(R.id.tilUsername)
        val tilPassword = findViewById<TextInputLayout>(R.id.tilPassword)
        val etUsername = findViewById<TextInputEditText>(R.id.etUsername)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString()

            // Validasi input (sesuai UC01 - Login Admin)
            tilUsername.error = if (username.isEmpty()) getString(R.string.sipeka_err_username_kosong) else null
            tilPassword.error = if (password.isEmpty()) getString(R.string.sipeka_err_password_kosong) else null
            if (username.isEmpty() || password.isEmpty()) return@setOnClickListener

            // TODO: ganti dengan request ke API Laravel (Retrofit) di commit berikutnya
            if (username == "admin" && password == "admin123") {
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, R.string.sipeka_err_login_gagal, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
