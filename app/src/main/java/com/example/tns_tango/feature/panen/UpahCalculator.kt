package com.example.tns_tango.feature.panen

/**
 * Hasil hitung upah satu kali panen (semua dalam rupiah).
 * File: app/src/main/java/com/example/tns_tango/feature/panen/UpahCalculator.kt
 *
 * @param upahKotor  berat x tarif per kg x koefisien lahan (sebelum potongan)
 * @param denda      denda grading buah mentah
 * @param upahBersih upah kotor dikurangi denda, tidak pernah di bawah 0
 */
data class HasilUpah(val upahKotor: Long, val denda: Long, val upahBersih: Long)

/**
 * Logika hitung upah otomatis (F03). Murni perhitungan, tanpa tampilan,
 * jadi mudah diuji dan nanti bisa dipakai ulang oleh layar Hasil Hitung.
 *
 * Rumus (grand design halaman 11):
 * Berat Buah x Koefisien Lahan = Upah Kotor, lalu dikurangi Denda Grading = Upah Bersih.
 *
 * Dipanggil oleh: InputPanenActivity.updateHasil()
 */
object UpahCalculator {

    /** Tarif dasar per kg (angka contoh). TODO: ambil dari layar Pengaturan Tarif. */
    const val TARIF_PER_KG = 3_500L

    /** Denda per janjang mentah (angka contoh). TODO: ambil dari Pengaturan Tarif. */
    const val DENDA_PER_JANJANG = 5_000L

    /**
     * Hitung upah kotor, denda, dan upah bersih.
     *
     * @param beratKg       berat buah dari timbangan; nilai negatif dianggap 0
     * @param lahan         jenis lahan (menentukan koefisien)
     * @param janjangMentah jumlah janjang mentah yang kena denda; nilai negatif dianggap 0
     * @return [HasilUpah] yang siap ditampilkan
     */
    fun hitung(beratKg: Double, lahan: JenisLahan, janjangMentah: Int): HasilUpah {
        val berat = beratKg.coerceAtLeast(0.0)
        val upahKotor = Math.round(berat * TARIF_PER_KG * lahan.koefisien)
        val denda = janjangMentah.coerceAtLeast(0) * DENDA_PER_JANJANG
        val upahBersih = (upahKotor - denda).coerceAtLeast(0L)
        return HasilUpah(upahKotor, denda, upahBersih)
    }
}
