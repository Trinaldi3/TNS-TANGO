package com.example.tns_tango.feature.riwayat

import android.content.Context
import com.example.tns_tango.feature.panen.BuktiPanenData
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

/** Penyimpanan riwayat panen berbasis JSON di internal storage; tidak memakai database. */
object RiwayatPanenStore {
    private const val FILE_NAME = "riwayat_panen.json"
    private const val PHOTO_DIR = "riwayat_panen_files"

    @Synchronized
    fun save(context: Context, data: BuktiPanenData): RiwayatItem {
        val id = "${System.currentTimeMillis()}_${(0..9999).random()}"
        val mediaDir = File(context.filesDir, PHOTO_DIR).apply { mkdirs() }

        val savedPhoto = data.fotoPath?.let { sourcePath ->
            val source = File(sourcePath)
            if (source.exists()) {
                File(mediaDir, "${id}_foto${source.extension.takeIf { it.isNotBlank() }?.let { ".$it" } ?: ".jpg"}")
                    .also { source.copyTo(it, overwrite = true) }.absolutePath
            } else null
        }

        val sourceSignature = File(data.tandaTanganPath)
        if (!sourceSignature.exists()) throw IllegalStateException("File tanda tangan tidak ditemukan")
        val savedSignature = File(mediaDir, "${id}_tanda_tangan.png")
        sourceSignature.copyTo(savedSignature, overwrite = true)

        val item = RiwayatItem(
            id = id,
            namaPemanen = data.namaPemanen,
            tanggal = data.tanggal,
            beratKg = data.beratKg,
            jenisLahan = data.jenisLahan,
            koefisienLahan = data.koefisienLahan,
            jumlahJanjangMentah = data.jumlahJanjangMentah,
            tarifPerKg = data.tarifPerKg,
            dendaPerJanjang = data.dendaPerJanjang,
            upahKotor = data.upahKotor,
            totalDenda = data.totalDenda,
            upahBersih = data.upahBersih,
            fotoPath = savedPhoto,
            tandaTanganPath = savedSignature.absolutePath
        )

        try {
            val all = getAll(context).toMutableList()
            all.add(item)
            writeAll(context, all)
        } catch (error: Exception) {
            savedPhoto?.let { File(it).delete() }
            savedSignature.delete()
            throw error
        }
        return item
    }

    @Synchronized
    fun getAll(context: Context): List<RiwayatItem> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return emptyList()
        val json = JSONArray(file.readText(Charsets.UTF_8))
        return buildList {
            for (index in 0 until json.length()) {
                val obj = json.optJSONObject(index) ?: continue
                add(obj.toRiwayatItem())
            }
        }.sortedByDescending { it.id }
    }

    fun getById(context: Context, id: String): RiwayatItem? =
        getAll(context).firstOrNull { it.id == id }

    private fun writeAll(context: Context, items: List<RiwayatItem>) {
        val array = JSONArray()
        items.forEach { item ->
            array.put(JSONObject().apply {
                put("id", item.id)
                put("namaPemanen", item.namaPemanen)
                put("tanggal", item.tanggal)
                put("beratKg", item.beratKg)
                put("jenisLahan", item.jenisLahan)
                put("koefisienLahan", item.koefisienLahan)
                put("jumlahJanjangMentah", item.jumlahJanjangMentah)
                put("tarifPerKg", item.tarifPerKg)
                put("dendaPerJanjang", item.dendaPerJanjang)
                put("upahKotor", item.upahKotor)
                put("totalDenda", item.totalDenda)
                put("upahBersih", item.upahBersih)
                put("fotoPath", item.fotoPath)
                put("tandaTanganPath", item.tandaTanganPath)
            })
        }
        val target = File(context.filesDir, FILE_NAME)
        val temporary = File(context.filesDir, "$FILE_NAME.tmp")
        FileOutputStream(temporary).use { it.write(array.toString().toByteArray(Charsets.UTF_8)) }
        if (target.exists() && !target.delete()) throw IllegalStateException("Tidak dapat memperbarui file riwayat")
        if (!temporary.renameTo(target)) {
            temporary.copyTo(target, overwrite = true)
            temporary.delete()
        }
    }

    private fun JSONObject.toRiwayatItem() = RiwayatItem(
        id = optString("id"),
        namaPemanen = optString("namaPemanen"),
        tanggal = optString("tanggal"),
        beratKg = optString("beratKg"),
        jenisLahan = optString("jenisLahan"),
        koefisienLahan = optString("koefisienLahan"),
        jumlahJanjangMentah = optInt("jumlahJanjangMentah"),
        tarifPerKg = optString("tarifPerKg"),
        dendaPerJanjang = optString("dendaPerJanjang"),
        upahKotor = optString("upahKotor"),
        totalDenda = optString("totalDenda"),
        upahBersih = optString("upahBersih"),
        fotoPath = if (isNull("fotoPath")) null else optString("fotoPath").takeIf { it.isNotBlank() },
        tandaTanganPath = optString("tandaTanganPath")
    )
}
