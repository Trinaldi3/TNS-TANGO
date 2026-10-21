package com.example.tns_tango.core.session

import android.content.Context
import android.content.SharedPreferences

/**
 * Menyimpan status login memakai SharedPreferences.
 * File: app/src/main/java/com/example/tns_tango/core/session/SessionManager.kt
 *
 * Siapa yang memanggil:
 * - saveLogin()  : LoginActivity saat autentikasi berhasil (isLogin = true)
 * - isLoggedIn() : SplashActivity (menentukan tujuan) dan MainActivity (pengaman)
 * - getUsername(): DashboardFragment (sapaan "Halo, ...") dan ProfileFragment
 * - logout()     : ProfileFragment saat tombol Keluar dikonfirmasi
 */
class SessionManager(context: Context) {

    /** File SharedPreferences "sipeka_session". Pakai applicationContext supaya tidak bocor Activity. */
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /** Simpan isLogin = true dan username setelah login berhasil. */
    fun saveLogin(username: String) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGIN, true)
            .putString(KEY_USERNAME, username)
            .apply()
    }

    /** Baca status login. Nilai awal false (belum pernah login). */
    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGIN, false)

    /** Baca username yang tersimpan; string kosong jika belum ada. */
    fun getUsername(): String = prefs.getString(KEY_USERNAME, "") ?: ""

    /** Hapus semua data sesi (isLogin ikut terhapus) saat Keluar. */
    fun logout() {
        prefs.edit().clear().apply()
    }

    companion object {
        const val PREF_NAME = "sipeka_session"
        const val KEY_IS_LOGIN = "isLogin"
        const val KEY_USERNAME = "username"
    }
}
