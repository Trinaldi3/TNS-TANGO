package com.example.tns_tango.feature.riwayat

import com.example.tns_tango.feature.panen.BuktiPanenData

/** Satu catatan panen yang disimpan secara lokal di perangkat. */
data class RiwayatItem(
    val id: String,
    val namaPemanen: String,
    val tanggal: String,
    val beratKg: String,
    val jenisLahan: String,
    val koefisienLahan: String,
    val jumlahJanjangMentah: Int,
    val tarifPerKg: String,
    val dendaPerJanjang: String,
    val upahKotor: String,
    val totalDenda: String,
    val upahBersih: String,
    val fotoPath: String?,
    val tandaTanganPath: String
) {
    fun toBuktiPanenData() = BuktiPanenData(
        namaPemanen = namaPemanen,
        tanggal = tanggal,
        beratKg = beratKg,
        jenisLahan = jenisLahan,
        koefisienLahan = koefisienLahan,
        jumlahJanjangMentah = jumlahJanjangMentah,
        tarifPerKg = tarifPerKg,
        dendaPerJanjang = dendaPerJanjang,
        upahKotor = upahKotor,
        totalDenda = totalDenda,
        upahBersih = upahBersih,
        fotoPath = fotoPath,
        tandaTanganPath = tandaTanganPath
    )
}
