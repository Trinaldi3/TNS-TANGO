package com.example.tns_tango.feature.panen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.OutputStream

/**
 * Data satu proses input panen yang akan dicetak ke bukti PDF.
 * Foto bersifat opsional; tanda tangan wajib karena divalidasi di form.
 */
data class BuktiPanenData(
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
)

/** Membuat satu halaman PDF A4 menggunakan API bawaan Android PdfDocument. */
object BuktiPanenPdf {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val LEFT = 42f
    private const val RIGHT = 553f

    fun write(output: OutputStream, data: BuktiPanenData) {
        val document = PdfDocument()
        var photo: Bitmap? = null
        var signature: Bitmap? = null

        try {
            photo = data.fotoPath
                ?.let { File(it) }
                ?.takeIf { it.exists() }
                ?.let { BitmapFactory.decodeFile(it.absolutePath) }
            signature = File(data.tandaTanganPath)
                .takeIf { it.exists() }
                ?.let { file -> BitmapFactory.decodeFile(file.absolutePath) }

            val pageInfo = PdfDocument.PageInfo.Builder(
                PAGE_WIDTH, PAGE_HEIGHT, 1
            ).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            canvas.drawColor(Color.WHITE)

            val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(28, 91, 63)
                textSize = 20f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(90, 100, 94)
                textSize = 9f
            }
            val sectionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(28, 91, 63)
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(90, 96, 92)
                textSize = 9.5f
            }
            val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(35, 42, 38)
                textSize = 9.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(220, 227, 222)
                strokeWidth = 0.8f
            }
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(205, 216, 208)
                style = Paint.Style.STROKE
                strokeWidth = 1f
            }

            canvas.drawText("BUKTI PERHITUNGAN UPAH PANEN", LEFT, 55f, titlePaint)
            canvas.drawText("Dokumen ringkasan hasil input panen SIPEKA", LEFT, 73f, subtitlePaint)
            canvas.drawLine(LEFT, 88f, RIGHT, 88f, linePaint)

            canvas.drawText("INFORMASI PANEN", LEFT, 112f, sectionPaint)
            var y = 137f
            drawRow(canvas, "Nama pemanen", data.namaPemanen, y, labelPaint, valuePaint, linePaint)
            y += 25f
            drawRow(canvas, "Tanggal input", data.tanggal, y, labelPaint, valuePaint, linePaint)
            y += 25f
            drawRow(canvas, "Berat hasil panen", "${data.beratKg} kg", y, labelPaint, valuePaint, linePaint)
            y += 25f
            drawRow(canvas, "Jenis lahan", "${data.jenisLahan} (koefisien ${data.koefisienLahan})", y, labelPaint, valuePaint, linePaint)
            y += 25f
            drawRow(canvas, "Janjang mentah", "${data.jumlahJanjangMentah} janjang", y, labelPaint, valuePaint, linePaint)
            y += 25f
            drawRow(canvas, "Tarif dasar", "${data.tarifPerKg} / kg", y, labelPaint, valuePaint, linePaint)
            y += 25f
            drawRow(canvas, "Upah kotor", data.upahKotor, y, labelPaint, valuePaint, linePaint)
            y += 25f
            drawRow(canvas, "Denda grading", "${data.totalDenda} (${data.dendaPerJanjang} / janjang)", y, labelPaint, valuePaint, linePaint)
            y += 31f

            val totalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(28, 91, 63)
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawRoundRect(RectF(LEFT, y - 15f, RIGHT, y + 18f), 7f, 7f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(235, 245, 238) })
            canvas.drawText("TOTAL UPAH BERSIH", LEFT + 12f, y + 6f, totalPaint)
            val totalWidth = totalPaint.measureText(data.upahBersih)
            canvas.drawText(data.upahBersih, RIGHT - 12f - totalWidth, y + 6f, totalPaint)

            y += 48f
            canvas.drawText("DOKUMENTASI FOTO KEGIATAN PANEN", LEFT, y, sectionPaint)
            val photoRect = RectF(LEFT, y + 12f, RIGHT, y + 166f)
            canvas.drawRoundRect(photoRect, 6f, 6f, borderPaint)
            if (photo != null) {
                drawBitmapFit(canvas, photo!!, RectF(photoRect.left + 5f, photoRect.top + 5f,
                    photoRect.right - 5f, photoRect.bottom - 5f))
            } else {
                val emptyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.rgb(130, 140, 134)
                    textSize = 10f
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText("Tidak ada foto dokumentasi untuk input ini",
                    photoRect.centerX(), photoRect.centerY(), emptyPaint)
            }

            y = photoRect.bottom + 29f
            canvas.drawText("TANDA TANGAN PEMANEN", LEFT, y, sectionPaint)
            val signRect = RectF(LEFT, y + 12f, LEFT + 220f, y + 91f)
            canvas.drawRoundRect(signRect, 6f, 6f, borderPaint)
            if (signature != null) {
                drawBitmapFit(canvas, signature!!, RectF(signRect.left + 5f, signRect.top + 5f,
                    signRect.right - 5f, signRect.bottom - 5f))
            }
            val signLabel = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(90, 96, 92)
                textSize = 9f
            }
            canvas.drawText(data.namaPemanen, signRect.left, signRect.bottom + 15f, signLabel)

            val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(125, 132, 127)
                textSize = 8f
            }
            canvas.drawLine(LEFT, 805f, RIGHT, 805f, linePaint)
            canvas.drawText("Dibuat melalui SIPEKA • Bukti perhitungan, bukan bukti pembayaran resmi", LEFT, 820f, footerPaint)

            document.finishPage(page)
            document.writeTo(output)
        } finally {
            photo?.recycle()
            signature?.recycle()
            document.close()
        }
    }

    private fun drawRow(
        canvas: Canvas,
        label: String,
        value: String,
        baseline: Float,
        labelPaint: Paint,
        valuePaint: Paint,
        linePaint: Paint
    ) {
        canvas.drawText(label, LEFT, baseline, labelPaint)
        val maxValueWidth = 300f
        val shownValue = ellipsize(value, valuePaint, maxValueWidth)
        canvas.drawText(shownValue, 250f, baseline, valuePaint)
        canvas.drawLine(LEFT, baseline + 7f, RIGHT, baseline + 7f, linePaint)
    }

    private fun ellipsize(text: String, paint: Paint, maxWidth: Float): String {
        if (paint.measureText(text) <= maxWidth) return text
        var shortened = text
        while (shortened.isNotEmpty() && paint.measureText("$shortened…") > maxWidth) {
            shortened = shortened.dropLast(1)
        }
        return "$shortened…"
    }

    private fun drawBitmapFit(canvas: Canvas, bitmap: Bitmap, target: RectF) {
        val scale = minOf(target.width() / bitmap.width, target.height() / bitmap.height)
        val width = bitmap.width * scale
        val height = bitmap.height * scale
        val destination = RectF(
            target.centerX() - width / 2f,
            target.centerY() - height / 2f,
            target.centerX() + width / 2f,
            target.centerY() + height / 2f
        )
        canvas.drawBitmap(bitmap, null, destination, Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
    }
}
