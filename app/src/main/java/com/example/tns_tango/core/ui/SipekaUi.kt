package com.example.tns_tango.core.ui

import android.graphics.Color
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.tns_tango.R
import com.google.android.material.appbar.MaterialToolbar

/**
 * Helper UI bersama untuk semua Activity dan Fragment SIPEKA.
 * File: app/src/main/java/com/example/tns_tango/core/ui/SipekaUi.kt
 *
 * Dipakai oleh: SplashActivity, LoginActivity, MainActivity (enableSipekaEdgeToEdge, applySystemBarInsets),
 * LoginActivity dan halaman form (setupSipekaToolbar), halaman form (applyBottomInsetsWithIme),
 * dan semua Fragment (applySystemBarInsets pada header).
 */

/**
 * Tampilan edge-to-edge dengan ikon status bar terang (latar header hijau tua).
 * Panggil di awal onCreate(), sebelum setContentView().
 */
fun ComponentActivity.enableSipekaEdgeToEdge() {
    enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
}

/**
 * Tambah padding setinggi status bar / navigation bar supaya konten tidak tertutup.
 *
 * @param top    true = tambah padding atas setinggi status bar (untuk header hijau)
 * @param bottom true = tambah padding bawah setinggi navigation bar (untuk Bottom Navigation)
 */
fun View.applySystemBarInsets(top: Boolean = false, bottom: Boolean = false) {
    // Simpan padding asli dari XML supaya tidak menumpuk tiap kali insets berubah
    val startTop = paddingTop
    val startBottom = paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val bars = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
        )
        v.updatePadding(
            top = if (top) startTop + bars.top else startTop,
            bottom = if (bottom) startBottom + bars.bottom else startBottom
        )
        insets
    }
}

/**
 * Padding bawah mengikuti navigation bar atau keyboard (mana yang lebih tinggi),
 * supaya kolom form tidak tertutup keyboard. Dipakai halaman form (Register With Gmail,
 * Registrasi).
 */
fun View.applyBottomInsetsWithIme() {
    val startBottom = paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
        v.updatePadding(bottom = startBottom + maxOf(bars.bottom, ime.bottom))
        insets
    }
}

/**
 * Pasang Toolbar SIPEKA (layout include_toolbar.xml) beserta tombol back.
 * Panggil SETELAH setContentView(). Sekarang hanya dipakai LoginActivity,
 * karena halaman lain sudah menjadi Fragment di dalam bottom navigation.
 *
 * @param title    judul Toolbar untuk halaman ini
 * @param showBack tampilkan tombol back (false hanya untuk halaman awal)
 * @param onBack   aksi khusus tombol back; default kembali ke Activity sebelumnya
 */
fun AppCompatActivity.setupSipekaToolbar(
    title: CharSequence,
    showBack: Boolean = true,
    onBack: (() -> Unit)? = null
) {
    val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
    findViewById<View>(R.id.appbarContainer).applySystemBarInsets(top = true)

    setSupportActionBar(toolbar)
    supportActionBar?.apply {
        this.title = title
        setDisplayHomeAsUpEnabled(showBack)
        if (showBack) setHomeAsUpIndicator(R.drawable.ic_arrow_back)
    }

    if (showBack) {
        toolbar.setNavigationOnClickListener {
            if (onBack != null) onBack() else onBackPressedDispatcher.onBackPressed()
        }
    }
}
