package com.example.tns_tango.feature.auth

import androidx.annotation.StringRes
import com.example.tns_tango.R

/**
 * Jenis kesalahan pada input email Gmail, beserta pesan yang ditampilkan di dialog.
 * File: app/src/main/java/com/example/tns_tango/feature/auth/AuthValidator.kt
 */
enum class EmailError(@StringRes val messageRes: Int) {
    KOSONG(R.string.email_err_kosong),
    BUKAN_GMAIL(R.string.email_err_bukan_gmail),
    FORMAT_SALAH(R.string.email_err_format)
}

/**
 * Hasil validasi form registrasi. Nilai null berarti field tersebut valid.
 *
 * @param nama     pesan error untuk kolom Nama
 * @param username pesan error untuk kolom Username
 * @param password pesan error untuk kolom Password
 */
data class RegisterErrors(
    @StringRes val nama: Int?,
    @StringRes val username: Int?,
    @StringRes val password: Int?
) {
    /** True jika ada minimal satu field yang tidak valid. */
    val hasError: Boolean get() = nama != null || username != null || password != null
}

/**
 * Aturan validasi untuk Register With Gmail dan Registrasi. Murni logika, tanpa tampilan.
 *
 * Dipanggil oleh: EmailInputActivity (validateGmail) dan RegisterActivity (validateRegistration).
 */
object AuthValidator {

    const val GMAIL_DOMAIN = "@gmail.com"
    const val MIN_PASSWORD_LENGTH = 6

    /** Rapikan email: buang spasi di tepi dan jadikan huruf kecil. */
    fun normalizeEmail(raw: String): String = raw.trim().lowercase()

    /**
     * Validasi email Gmail: tidak boleh kosong, harus berakhiran @gmail.com,
     * dan harus ada nama di depan @ (tanpa spasi dan tanpa @ kedua).
     *
     * @return jenis kesalahan, atau null jika email valid
     */
    fun validateGmail(raw: String): EmailError? {
        val email = normalizeEmail(raw)
        if (email.isEmpty()) return EmailError.KOSONG
        if (!email.endsWith(GMAIL_DOMAIN)) return EmailError.BUKAN_GMAIL

        val name = email.removeSuffix(GMAIL_DOMAIN)
        if (name.isEmpty() || name.contains(' ') || name.contains('@')) return EmailError.FORMAT_SALAH
        return null
    }

    /**
     * Validasi form registrasi:
     * - semua field wajib diisi
     * - username tidak boleh mengandung spasi
     * - password minimal [MIN_PASSWORD_LENGTH] karakter
     *
     * @return [RegisterErrors] berisi pesan error per field
     */
    fun validateRegistration(nama: String, username: String, password: String): RegisterErrors {
        val namaError = if (nama.isBlank()) R.string.reg_err_wajib else null

        val usernameError = when {
            username.isEmpty() -> R.string.reg_err_wajib
            username.any { it.isWhitespace() } -> R.string.reg_err_username_spasi
            else -> null
        }

        val passwordError = when {
            password.isEmpty() -> R.string.reg_err_wajib
            password.length < MIN_PASSWORD_LENGTH -> R.string.reg_err_password_pendek
            else -> null
        }

        return RegisterErrors(namaError, usernameError, passwordError)
    }
}
