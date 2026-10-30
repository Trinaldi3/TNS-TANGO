package com.example.tns_tango.feature.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.tns_tango.R
import com.example.tns_tango.core.session.SessionManager
import com.example.tns_tango.core.ui.applySystemBarInsets
import com.example.tns_tango.core.ui.enableSipekaEdgeToEdge
import com.example.tns_tango.databinding.ActivityMainBinding
import com.example.tns_tango.feature.auth.LoginActivity
import com.example.tns_tango.feature.website.WebsiteFragment

/**
 * Halaman utama SIPEKA Mobile: tempat Bottom Navigation dan Fragment.
 * Setelah login pengguna langsung masuk ke tab Dashboard.
 * Sesuai ketentuan: halaman awal TIDAK memakai Toolbar dan tombol back.
 * File: app/src/main/java/com/example/tns_tango/feature/main/MainActivity.kt
 *
 * Alur kode (flow code):
 * onCreate -> cek session -> setupBottomNavigation()
 *   -> item diklik -> MainTab.fromMenuId() -> showTab() -> Fragment tampil di fragmentContainer
 * Tombol back sistem -> setupBackNavigation()
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var session: SessionManager

    /** Tab yang sedang tampil; disimpan supaya tidak hilang saat layar diputar. */
    private var currentTab: MainTab = MainTab.DASHBOARD

    /** Pengaman login, pasang layout, lalu siapkan Bottom Navigation dan tombol back. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableSipekaEdgeToEdge()

        session = SessionManager(this)

        // Pengaman: kalau belum login, kembali ke halaman Login
        if (!session.isLoggedIn()) {
            goToLogin()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Bottom Navigation tidak boleh tertutup navigation bar sistem
        binding.bottomNav.applySystemBarInsets(bottom = true)

        setupBottomNavigation(savedInstanceState)
        setupBackNavigation()
    }

    /**
     * Sambungkan Bottom Navigation ke Fragment.
     * Pertama kali dibuka: tampilkan tab Dashboard. Setelah layar diputar:
     * FragmentManager sudah memulihkan Fragment, jadi cukup ingat tab terakhir.
     */
    private fun setupBottomNavigation(savedInstanceState: Bundle?) {
        binding.bottomNav.setOnItemSelectedListener { item ->
            val tab = MainTab.fromMenuId(item.itemId) ?: return@setOnItemSelectedListener false
            showTab(tab)
            true
        }
        // Menekan menu yang sudah aktif tidak melakukan apa-apa
        binding.bottomNav.setOnItemReselectedListener { }

        if (savedInstanceState == null) {
            showTab(MainTab.DASHBOARD)
        } else {
            currentTab = MainTab.fromTag(savedInstanceState.getString(KEY_CURRENT_TAB))
                ?: MainTab.DASHBOARD
        }
    }

    /**
     * Tampilkan Fragment milik [tab] dan sembunyikan yang lain (show/hide).
     * Fragment dibuat sekali lalu dipertahankan, jadi halaman Website tidak memuat ulang.
     */
    private fun showTab(tab: MainTab) {
        val fm = supportFragmentManager
        val transaction = fm.beginTransaction().setReorderingAllowed(true)

        // Sembunyikan semua Fragment yang sedang ada
        fm.fragments.forEach { transaction.hide(it) }

        // Ambil Fragment tab ini; kalau belum ada, buat dan tambahkan
        val existing: Fragment? = fm.findFragmentByTag(tab.tag)
        if (existing == null) {
            transaction.add(R.id.fragmentContainer, tab.create(), tab.tag)
        } else {
            transaction.show(existing)
        }

        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()
        currentTab = tab
    }

    /** Pindah ke tab Riwayat setelah satu input panen berhasil disimpan. */
    fun openRiwayatTab() {
        if (::binding.isInitialized) {
            binding.bottomNav.selectedItemId = MainTab.RIWAYAT.menuId
        }
    }

    /**
     * Aturan tombol back sistem:
     * 1. Di tab Website dan halaman web bisa mundur -> mundur satu halaman web
     * 2. Di tab selain Dashboard -> pindah ke tab Dashboard
     * 3. Di tab Dashboard -> keluar dari aplikasi
     */
    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val website = supportFragmentManager.findFragmentByTag(MainTab.WEBSITE.tag)
                    as? WebsiteFragment

                when {
                    currentTab == MainTab.WEBSITE && website?.goBackInPage() == true -> Unit
                    currentTab != MainTab.DASHBOARD ->
                        binding.bottomNav.selectedItemId = MainTab.DASHBOARD.menuId
                    else -> {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        })
    }

    /** Simpan tab aktif sebelum Activity dihancurkan (misalnya layar diputar). */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_CURRENT_TAB, currentTab.tag)
    }

    /** Buka LoginActivity dan kosongkan tumpukan halaman. */
    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    companion object {
        private const val KEY_CURRENT_TAB = "current_tab"
    }
}
