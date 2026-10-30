
package com.example.tns_tango.core.session

import android.content.Context
import android.content.SharedPreferences

/**
 * Data hasil registrasi.
 */
data class RegistrationData(
    val nama: String,
    val email: String,
    val username: String,
    val password: String
)

/**
 * Menyimpan dan membaca data registrasi menggunakan SharedPreferences.
 */
class RegistrationStore(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

    /**
     * Menyimpan seluruh data registrasi.
     */
    fun save(data: RegistrationData) {
        prefs.edit()
            .putBoolean(KEY_REGISTERED, true)
            .putString(KEY_NAMA, data.nama)
            .putString(KEY_EMAIL, data.email)
            .putString(KEY_USERNAME, data.username)
            .putString(KEY_PASSWORD, data.password)
            .apply()
    }

    /**
     * Membaca data registrasi.
     */
    fun load(): RegistrationData? {
        if (!prefs.getBoolean(KEY_REGISTERED, false)) {
            return null
        }

        return RegistrationData(
            nama = prefs.getString(KEY_NAMA, "").orEmpty(),
            email = prefs.getString(KEY_EMAIL, "").orEmpty(),
            username = prefs.getString(KEY_USERNAME, "").orEmpty(),
            password = prefs.getString(KEY_PASSWORD, "").orEmpty()
        )
    }

    /**
     * Memeriksa kecocokan username dan password.
     */
    fun isMatch(username: String, password: String): Boolean {
        val saved = load() ?: return false

        return username == saved.username &&
                password == saved.password
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
