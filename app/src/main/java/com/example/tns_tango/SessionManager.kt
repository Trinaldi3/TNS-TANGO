package com.example.tns_tango

import android.content.Context
import android.content.SharedPreferences

/**
 * Menyimpan status login memakai SharedPreferences.
 * File: app/src/main/java/com/example/tns_tango/SessionManager.kt
 *
 * - saveLogin()  : dipanggil LoginActivity saat autentikasi berhasil (isLogin = true)
 * - isLoggedIn() : dibaca SplashActivity untuk menentukan halaman tujuan
 * - logout()     : dipanggil MainActivity saat tombol Keluar (isLogin dihapus)
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveLogin(username: String) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGIN, true)
            .putString(KEY_USERNAME, username)
            .apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGIN, false)

    fun getUsername(): String = prefs.getString(KEY_USERNAME, "") ?: ""

    fun logout() {
        prefs.edit().clear().apply()
    }

    companion object {
        const val PREF_NAME = "sipeka_session"
        const val KEY_IS_LOGIN = "isLogin"
        const val KEY_USERNAME = "username"
    }
}
