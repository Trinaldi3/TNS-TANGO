package com.example.tns_tango.feature.main

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.example.tns_tango.R
import com.example.tns_tango.feature.about.AboutFragment
import com.example.tns_tango.feature.dashboard.DashboardFragment
import com.example.tns_tango.feature.profile.ProfileFragment
import com.example.tns_tango.feature.riwayat.RiwayatFragment
import com.example.tns_tango.feature.website.WebsiteFragment

/**
 * Daftar 5 menu Bottom Navigation: id menu -> tag Fragment -> cara membuat Fragment.
 * File: app/src/main/java/com/example/tns_tango/feature/main/MainTab.kt
 *
 * Untuk menambah menu baru: tambah item di res/menu/menu_bottom_nav.xml
 * lalu tambah satu baris enum di sini. MainActivity tidak perlu diubah.
 *
 * @param menuId id item di menu_bottom_nav.xml
 * @param tag    nama unik Fragment di FragmentManager
 * @param create pembuat Fragment baru (dipanggil sekali, saat tab pertama kali dibuka)
 */
enum class MainTab(
    @IdRes val menuId: Int,
    val tag: String,
    val create: () -> Fragment
) {
    DASHBOARD(R.id.nav_dashboard, "tab_dashboard", ::DashboardFragment),
    RIWAYAT(R.id.nav_riwayat, "tab_riwayat", ::RiwayatFragment),
    WEBSITE(R.id.nav_website, "tab_website", ::WebsiteFragment),
    ABOUT(R.id.nav_about, "tab_about", ::AboutFragment),
    PROFILE(R.id.nav_profile, "tab_profile", ::ProfileFragment);

    companion object {
        /** Cari tab dari id menu yang diklik; null jika id tidak dikenal. */
        fun fromMenuId(@IdRes id: Int): MainTab? = values().firstOrNull { it.menuId == id }

        /** Cari tab dari tag yang tersimpan (dipakai saat Activity dibuat ulang). */
        fun fromTag(tag: String?): MainTab? = values().firstOrNull { it.tag == tag }
    }
}
