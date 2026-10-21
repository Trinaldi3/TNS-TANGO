package com.example.tns_tango.feature.panen

import androidx.annotation.StringRes
import com.example.tns_tango.R

/**
 * Jenis lahan tempat buah dipanen. Koefisien memengaruhi upah kotor.
 * File: app/src/main/java/com/example/tns_tango/feature/panen/JenisLahan.kt
 *
 * Sesuai grand design (halaman 11): Datar = koefisien 1.0, Sulit = ditetapkan Owner.
 * Angka Sulit di sini adalah contoh; nanti diambil dari layar Pengaturan Tarif.
 *
 * @param labelRes teks yang tampil di chip dan ringkasan (dari strings_panen.xml)
 * @param koefisien pengali upah untuk jenis lahan ini
 */
enum class JenisLahan(@StringRes val labelRes: Int, val koefisien: Double) {
    DATAR(R.string.panen_lahan_datar, 1.0),
    SULIT(R.string.panen_lahan_sulit, 1.2)
}
