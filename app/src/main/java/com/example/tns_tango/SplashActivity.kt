package com.example.tns_tango

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity

/**
 * Splash screen SIPEKA (halaman pertama saat aplikasi dibuka).
 * File: app/src/main/java/com/example/tns_tango/SplashActivity.kt
 *
 * Alur kode:
 * onCreate -> tunggu 2 detik (Handler) -> openNextScreen()
 *   - SharedPreferences isLogin = true  -> MainActivity
 *   - SharedPreferences isLogin = false -> LoginActivity
 */
class SplashActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableSipekaEdgeToEdge()
        setContentView(R.layout.activity_splash)
        findViewById<View>(R.id.rootSplash).applySystemBarInsets(top = true, bottom = true)

        session = SessionManager(this)
        handler.postDelayed({ openNextScreen() }, SPLASH_DELAY_MS)
    }

    private fun openNextScreen() {
        val target = if (session.isLoggedIn()) MainActivity::class.java else LoginActivity::class.java
        startActivity(Intent(this, target))
        finish()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    companion object {
        private const val SPLASH_DELAY_MS = 2000L
    }
}
