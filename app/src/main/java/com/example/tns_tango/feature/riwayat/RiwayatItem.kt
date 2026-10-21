package com.example.tns_tango.feature.riwayat

/**
 * Status pembayaran upah satu hari panen.
 * File: app/src/main/java/com/example/tns_tango/feature/riwayat/RiwayatItem.kt
 */
enum class PayStatus { TERVALIDASI, BELUM_DIBAYAR }

/**
 * Satu baris riwayat gaji (model data).
 *
 * @param id         nomor unik, dipakai DiffUtil untuk membandingkan item
 * @param tanggal    tanggal panen, contoh "1 Okt"
 * @param beratKg    berat buah dari timbangan (kg)
 * @param upahBersih upah bersih dalam rupiah (berat x koefisien lahan - denda)
 * @param status     status pembayaran
 */
data class RiwayatItem(
    val id: Int,
    val tanggal: String,
    val beratKg: Int,
    val upahBersih: Long,
    val status: PayStatus
)

/**
 * Data contoh riwayat gaji. Akan diganti data dari Room DB / API Laravel di pertemuan berikutnya.
 */
object RiwayatSampleData {

    /** Kembalikan daftar riwayat contoh (angka mengikuti contoh di grand design). */
    fun items(): List<RiwayatItem> = listOf(
        RiwayatItem(1, "1 Okt", 63, 215_000, PayStatus.TERVALIDASI),
        RiwayatItem(2, "2 Okt", 58, 198_000, PayStatus.TERVALIDASI),
        RiwayatItem(3, "3 Okt", 55, 187_500, PayStatus.BELUM_DIBAYAR),
        RiwayatItem(4, "4 Okt", 59, 201_000, PayStatus.BELUM_DIBAYAR),
        RiwayatItem(5, "5 Okt", 61, 208_000, PayStatus.TERVALIDASI),
        RiwayatItem(6, "6 Okt", 57, 194_500, PayStatus.BELUM_DIBAYAR)
    )
}
