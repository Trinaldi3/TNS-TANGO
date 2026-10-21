package com.example.tns_tango.feature.panen

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import com.example.tns_tango.R
import com.example.tns_tango.core.ui.enableSipekaEdgeToEdge
import com.example.tns_tango.core.ui.setupSipekaToolbar
import com.example.tns_tango.core.util.formatRupiah
import com.example.tns_tango.databinding.ActivityInputPanenBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale

/**
 * Form Input Panen: isi data, upah dihitung otomatis, pemanen tanda tangan, lalu simpan.
 * Gabungan layar 7 (Input Panen), 8 (Hasil Hitung), dan 9 (Validasi Bayar) di grand design.
 * Dibuka dari tombol "Input Panen" di DashboardFragment.
 * File: app/src/main/java/com/example/tns_tango/feature/panen/InputPanenActivity.kt
 *
 * Alur kode:
 * onCreate -> setupForm() -> setupSignature() -> btnSimpan
 * Mengetik berat/janjang atau memilih lahan -> updateHasil() -> UpahCalculator.hitung()
 * btnSimpan -> simpan() -> validateForm() -> saveSignature() -> showResultDialog()
 *
 * Catatan: data belum masuk ke Riwayat karena Room DB belum dibuat.
 * Tanda tangan disimpan sebagai PNG di penyimpanan internal aplikasi.
 */
class InputPanenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputPanenBinding

    /** Jenis lahan yang sedang dipilih (awal: Datar). */
    private var lahan = JenisLahan.DATAR

    /** Hasil hitung terakhir, dipakai lagi saat menyimpan. */
    private var hasil = HasilUpah(0L, 0L, 0L)

    /** Pasang layout, Toolbar + tombol back, lalu siapkan form, tanda tangan, dan tombol simpan. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableSipekaEdgeToEdge()
        binding = ActivityInputPanenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSipekaToolbar(getString(R.string.title_input_panen), onBack = { finish() })
        setupBottomInsets()

        setupForm()
        setupSignature()
        binding.btnSimpan.setOnClickListener { simpan() }

        updateHasil()
    }

    /** Padding bawah mengikuti navigation bar atau keyboard (mana yang lebih tinggi). */
    private fun setupBottomInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            view.updatePadding(bottom = maxOf(bars.bottom, ime.bottom))
            insets
        }
    }

    /** Sambungkan kolom berat, kolom janjang, dan chip lahan ke updateHasil(). */
    private fun setupForm() {
        binding.etBerat.doAfterTextChanged {
            binding.tilBerat.error = null
            updateHasil()
        }
        binding.etJanjang.doAfterTextChanged { updateHasil() }
        binding.etNama.doAfterTextChanged { binding.tilNama.error = null }

        binding.chipGroupLahan.setOnCheckedStateChangeListener { _, checkedIds ->
            lahan = if (checkedIds.firstOrNull() == R.id.chipSulit) JenisLahan.SULIT else JenisLahan.DATAR
            updateHasil()
        }
    }

    /** Sambungkan kotak tanda tangan dan tombol Hapus. */
    private fun setupSignature() {
        binding.signaturePad.onSignedChanged = { signed ->
            binding.tvTtdHint.visibility = if (signed) View.GONE else View.VISIBLE
            if (signed) binding.tvTtdError.visibility = View.GONE
        }
        binding.btnHapusTtd.setOnClickListener { binding.signaturePad.clear() }
    }

    /** Baca berat dari kolom; koma dianggap titik. Kosong atau salah format = 0. */
    private fun readBerat(): Double =
        binding.etBerat.text.toString().replace(',', '.').toDoubleOrNull() ?: 0.0

    /** Baca jumlah janjang mentah dari kolom. Kosong = 0. */
    private fun readJanjang(): Int = binding.etJanjang.text.toString().toIntOrNull() ?: 0

    /**
     * Hitung ulang upah lalu tampilkan ke kartu hasil.
     * Dipanggil setiap berat/janjang berubah atau chip lahan diganti.
     */
    private fun updateHasil() {
        hasil = UpahCalculator.hitung(readBerat(), lahan, readJanjang())

        binding.tvUpahKotor.text = formatRupiah(hasil.upahKotor)
        binding.tvDenda.text =
            if (hasil.denda > 0) "- " + formatRupiah(hasil.denda) else formatRupiah(0L)
        binding.tvUpahBersih.text = formatRupiah(hasil.upahBersih)

        binding.tvInfoTarif.text = getString(
            R.string.panen_info_tarif,
            formatRupiah(UpahCalculator.TARIF_PER_KG),
            getString(lahan.labelRes),
            String.format(Locale("id", "ID"), "%.1f", lahan.koefisien),
            formatRupiah(UpahCalculator.DENDA_PER_JANJANG)
        )
    }

    /** Tombol Simpan: validasi, simpan tanda tangan, lalu tampilkan ringkasan. */
    private fun simpan() {
        if (!validateForm()) return

        val file = saveSignature()
        if (file == null) {
            Toast.makeText(this, R.string.panen_save_gagal, Toast.LENGTH_SHORT).show()
            return
        }
        showResultDialog()
    }

    /**
     * Cek form: nama terisi, berat lebih dari 0, tanda tangan tidak kosong.
     * Pesan error tampil di kolom/bagian yang bermasalah.
     * @return true jika semua valid
     */
    private fun validateForm(): Boolean {
        val namaOk = binding.etNama.text.toString().isNotBlank()
        val beratOk = readBerat() > 0.0
        val ttdOk = !binding.signaturePad.isEmpty()

        binding.tilNama.error = if (namaOk) null else getString(R.string.panen_err_nama)
        binding.tilBerat.error = if (beratOk) null else getString(R.string.panen_err_berat)
        binding.tvTtdError.visibility =
            if (ttdOk) View.GONE else View.VISIBLE

        return namaOk && beratOk && ttdOk
    }

    /**
     * Simpan tanda tangan sebagai PNG di folder tanda_tangan (penyimpanan internal aplikasi).
     * @return file yang tersimpan, atau null jika gagal
     */
    private fun saveSignature(): File? {
        return try {
            val dir = File(filesDir, "tanda_tangan").apply { mkdirs() }
            val file = File(dir, "ttd_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                binding.signaturePad.toBitmap().compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            file
        } catch (e: IOException) {
            null
        }
    }

    /** Dialog ringkasan: pilih Selesai (tutup form) atau Input lagi (kosongkan form). */
    private fun showResultDialog() {
        val message = getString(
            R.string.panen_dialog_message,
            binding.etNama.text.toString().trim(),
            binding.etBerat.text.toString().trim(),
            getString(lahan.labelRes),
            formatRupiah(hasil.upahKotor),
            formatRupiah(hasil.denda),
            formatRupiah(hasil.upahBersih)
        )
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.panen_dialog_title)
            .setMessage(message)
            .setCancelable(false)
            .setNegativeButton(R.string.panen_dialog_again) { _, _ -> resetForm() }
            .setPositiveButton(R.string.panen_dialog_done) { _, _ -> finish() }
            .show()
    }

    /** Kosongkan semua kolom, pilih lahan Datar, hapus tanda tangan, lalu hitung ulang. */
    private fun resetForm() {
        binding.etNama.text?.clear()
        binding.etBerat.text?.clear()
        binding.etJanjang.text?.clear()
        binding.chipDatar.isChecked = true
        binding.signaturePad.clear()
        binding.tilNama.error = null
        binding.tilBerat.error = null
        binding.tvTtdError.visibility = View.GONE
        binding.scrollPanen.scrollTo(0, 0)
        updateHasil()
    }
}
