package com.example.tns_tango.core.session

import android.content.Context
import android.content.SharedPreferences

/**
 * Data hasil registrasi.
 * File: app/src/main/java/com/example/tns_tango/core/session/RegistrationStore.kt
 *
 * @param nama     nama lengkap pengguna
 * @param email    email Gmail dari halaman input email (tidak bisa diubah di form)
 * @param username username untuk login (tanpa spasi)
 * @param password password untuk login (minimal 6 karakter)
 */
data class RegistrationData(
    val nama: String,
    val email: String,
    val username: String,
    val password: String
)

/**
 * Menyimpan dan membaca data registrasi di SharedPreferences (file "sipeka_registration").
 * Hanya satu akun yang tersimpan: registrasi baru menimpa registrasi lama.
 *
 * Siapa yang memanggil:
 * - save()    : RegisterActivity saat validasi berhasil
 * - load()    : RegisterSuccessActivity (menampilkan ringkasan)
 * - isMatch() : LoginActivity (kondisi login ke-2)
 *
 * Catatan: untuk keperluan quiz, password disimpan apa adanya (teks biasa).
 * Di aplikasi sungguhan password tidak boleh disimpan seperti ini; pakai hash di server.
 */
class RegistrationStore(context: Context) {

    /** SharedPreferences khusus data registrasi, terpisah dari status login. */
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /** Simpan seluruh data registrasi sekaligus. apply() wajib dipanggil agar tersimpan. */
    fun save(data: RegistrationData) {
        prefs.edit()
            .putBoolean(KEY_REGISTERED, true)
            .putString(KEY_NAMA, data.nama)
            .putString(KEY_EMAIL, data.email)
            .putString(KEY_USERNAME, data.username)
            .putString(KEY_PASSWORD, data.password)
    }

    /** Baca data registrasi; null jika belum pernah registrasi. */
    fun load(): RegistrationData? {
        if (!prefs.getBoolean(KEY_REGISTERED, false)) return null
        return RegistrationData(
            nama = prefs.getString(KEY_NAMA, "").orEmpty(),
            email = prefs.getString(KEY_EMAIL, "").orEmpty(),
            username = prefs.getString(KEY_USERNAME, "").orEmpty(),
            password = prefs.getString(KEY_PASSWORD, "").orEmpty()
        )
    }

    /**
     * Cek apakah username dan password sama dengan data registrasi yang tersimpan.
     * @return true jika sudah ada registrasi dan keduanya cocok persis
     */
    fun isMatch(username: String, password: String): Boolean {
        val saved = load() ?: return false
        return username == saved.username && password == saved.password
    }

    companion object {
        const val PREF_NAME = "sipeka_registration"
        const val KEY_REGISTERED = "registered"
        const val KEY_NAMA = "nama"
        const val KEY_EMAIL = "email"
        const val KEY_USERNAME = "username"
        const val KEY_PASSWORD = "password"
    }
}
