package com.example.tns_tango.core.util

import java.text.NumberFormat
import java.util.Locale

/**
 * Helper format tampilan yang dipakai lebih dari satu fitur.
 * File: app/src/main/java/com/example/tns_tango/core/util/Format.kt
 */

/**
 * Ubah angka rupiah menjadi teks "Rp 215.000" (pemisah ribuan titik, sesuai grand design).
 * Dipakai oleh RiwayatAdapter untuk menampilkan upah bersih.
 */
fun formatRupiah(amount: Long): String {
    val number = NumberFormat.getNumberInstance(Locale("id", "ID")).format(amount)
    return "Rp $number"
}
