package com.example.tns_tango.feature.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.tns_tango.R
import com.example.tns_tango.core.session.SessionManager
import com.example.tns_tango.core.ui.applySystemBarInsets
import com.example.tns_tango.core.ui.enableSipekaEdgeToEdge
import com.example.tns_tango.feature.auth.LoginActivity
import com.example.tns_tango.feature.main.MainActivity

/**
 * Splash screen SIPEKA (halaman pertama saat aplikasi dibuka).
 * File: app/src/main/java/com/example/tns_tango/feature/splash/SplashActivity.kt
 *
 * Alur kode:
 * onCreate -> tunggu 2 detik (Handler) -> openNextScreen()
 *   - SharedPreferences isLogin = true  -> MainActivity (langsung tab Dashboard)
 *   - SharedPreferences isLogin = false -> LoginActivity
 */
class SplashActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var session: SessionManager

    /** Siapkan layout, lalu jadwalkan pindah halaman 2 detik kemudian. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableSipekaEdgeToEdge()
        setContentView(R.layout.activity_splash)
        findViewById<View>(R.id.rootSplash).applySystemBarInsets(top = true, bottom = true)

        session = SessionManager(this)
        handler.postDelayed({ openNextScreen() }, SPLASH_DELAY_MS)
    }

    /** Cek isLogin di SharedPreferences, buka MainActivity atau LoginActivity, lalu tutup Splash. */
    private fun openNextScreen() {
        val target = if (session.isLoggedIn()) MainActivity::class.java else LoginActivity::class.java
        startActivity(Intent(this, target))
        finish()
    }

    /** Batalkan jadwal Handler supaya tidak pindah halaman setelah Activity ditutup. */
    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    companion object {
        private const val SPLASH_DELAY_MS = 2000L
    }
}
