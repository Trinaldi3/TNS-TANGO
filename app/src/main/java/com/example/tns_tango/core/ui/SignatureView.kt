package com.example.tns_tango.core.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.example.tns_tango.R

/**
 * Kotak tanda tangan digital: pengguna menggambar dengan jari.
 * File: app/src/main/java/com/example/tns_tango/core/ui/SignatureView.kt
 *
 * Alur kode:
 * jari menyentuh -> onTouchEvent() -> Path digambar -> invalidate() -> onDraw()
 * Panggil clear() untuk menghapus, isEmpty() untuk mengecek, toBitmap() untuk menyimpan.
 *
 * Dipakai oleh: InputPanenActivity (nanti juga layar Validasi Bayar).
 */
class SignatureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /** Garis tanda tangan yang sudah digambar. */
    private val path = Path()

    /** Kuas: hijau tua (Green 700), tebal 3dp, ujung membulat. */
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.sipeka_green_700)
        style = Paint.Style.STROKE
        strokeWidth = 3f * resources.displayMetrics.density
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private var lastX = 0f
    private var lastY = 0f
    private var hasInk = false

    /** Dipanggil saat status kosong/terisi berubah (true = sudah ada tanda tangan). */
    var onSignedChanged: ((Boolean) -> Unit)? = null

    /** Gambar garis tanda tangan di atas latar view. */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
    }

    /**
     * Tangani sentuhan jari: DOWN mulai garis, MOVE menyambung garis halus,
     * UP/CANCEL selesai. Parent (ScrollView) diminta tidak ikut menggulung saat menggambar.
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                parent?.requestDisallowInterceptTouchEvent(true)
                path.moveTo(x, y)
                lastX = x
                lastY = y
            }
            MotionEvent.ACTION_MOVE -> {
                // Kurva halus: titik tengah antara titik lama dan baru
                path.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2)
                lastX = x
                lastY = y
                markSigned()
            }
            MotionEvent.ACTION_UP -> {
                parent?.requestDisallowInterceptTouchEvent(false)
                performClick()
            }
            MotionEvent.ACTION_CANCEL ->
                parent?.requestDisallowInterceptTouchEvent(false)
            else -> return false
        }
        invalidate()
        return true
    }

    /** Wajib dipanggil untuk aksesibilitas saat jari diangkat. */
    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    /** Tandai sudah ada tanda tangan; beri tahu listener hanya saat status berubah. */
    private fun markSigned() {
        if (!hasInk) {
            hasInk = true
            onSignedChanged?.invoke(true)
        }
    }

    /** Hapus seluruh tanda tangan. */
    fun clear() {
        path.reset()
        val wasSigned = hasInk
        hasInk = false
        invalidate()
        if (wasSigned) onSignedChanged?.invoke(false)
    }

    /** True jika belum ada tanda tangan sama sekali. */
    fun isEmpty(): Boolean = !hasInk

    /** Ubah tanda tangan menjadi gambar (latar putih) agar bisa disimpan sebagai PNG. */
    fun toBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width.coerceAtLeast(1), height.coerceAtLeast(1), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        canvas.drawPath(path, paint)
        return bitmap
    }
}
