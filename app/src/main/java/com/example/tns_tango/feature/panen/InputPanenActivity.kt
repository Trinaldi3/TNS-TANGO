package com.example.tns_tango.feature.panen

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.tns_tango.feature.riwayat.RiwayatPanenStore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Form Input Panen: isi data, upah dihitung otomatis, pemanen tanda tangan, lalu simpan ke Riwayat.
 * Gabungan layar 7 (Input Panen), 8 (Hasil Hitung), dan 9 (Validasi Bayar) di grand design.
 * Dibuka dari tombol "Input Panen" di DashboardFragment.
 * File: app/src/main/java/com/example/tns_tango/feature/panen/InputPanenActivity.kt
 *
 * Alur kode:
 * onCreate -> setupForm() -> setupSignature() -> btnSimpan
 * Mengetik berat/janjang atau memilih lahan -> updateHasil() -> UpahCalculator.hitung()
 * btnSimpan -> simpan() -> validateForm() -> saveSignature() -> RiwayatPanenStore.save()
 *
 * Riwayat disimpan sebagai JSON di penyimpanan internal aplikasi, tanpa database. Foto dan tanda tangan disalin ke folder data riwayat.
 */
class InputPanenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputPanenBinding

    /** Jenis lahan yang sedang dipilih (awal: Datar). */
    private var lahan = JenisLahan.DATAR

    /** Hasil hitung terakhir, dipakai lagi saat menyimpan. */
    private var hasil = HasilUpah(0L, 0L, 0L)

    /** File foto dokumentasi untuk proses input panen yang sedang berlangsung. */
    private var fotoPanenFile: File? = null
    private var tandaTanganFile: File? = null

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val path = result.data?.getStringExtra(CameraPanenActivity.EXTRA_FOTO_PATH)
                val file = path?.let(::File)
                if (file != null && file.exists()) {
                    fotoPanenFile?.takeIf { it.absolutePath != file.absolutePath }?.delete()
                    fotoPanenFile = file
                    tampilkanPratinjauFoto(file)
                } else {
                    Toast.makeText(this, R.string.panen_foto_tidak_tersedia, Toast.LENGTH_SHORT).show()
                }
            }
        }

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
        setupDokumentasiFoto()
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

    /** Sambungkan tombol kamera dan hapus foto dokumentasi. */
    private fun setupDokumentasiFoto() {
        binding.btnAmbilFoto.setOnClickListener {
            cameraLauncher.launch(Intent(this, CameraPanenActivity::class.java))
        }
        binding.btnHapusFoto.setOnClickListener { hapusFotoPanen() }
    }

    /** Tampilkan foto yang dikembalikan CameraPanenActivity pada form input. */
    private fun tampilkanPratinjauFoto(file: File) {
        binding.ivFotoPanen.setImageURI(Uri.fromFile(file))
        binding.ivFotoPanen.visibility = View.VISIBLE
        binding.btnAmbilFoto.text = getString(R.string.panen_btn_ulang_foto)
        binding.btnHapusFoto.visibility = View.VISIBLE
    }

    /** Hapus foto dari form dan penyimpanan internal aplikasi. */
    private fun hapusFotoPanen() {
        fotoPanenFile?.delete()
        fotoPanenFile = null
        binding.ivFotoPanen.setImageDrawable(null)
        binding.ivFotoPanen.visibility = View.GONE
        binding.btnAmbilFoto.text = getString(R.string.panen_btn_ambil_foto)
        binding.btnHapusFoto.visibility = View.GONE
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

    /** Validasi data, simpan foto/tanda tangan bersama riwayat lokal, lalu kembali ke tab Riwayat. */
    private fun simpan() {
        if (!validateForm()) return

        val signature = saveSignature()
        if (signature == null) {
            Toast.makeText(this, R.string.panen_save_gagal, Toast.LENGTH_SHORT).show()
            return
        }

        val tanggal = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
            .format(Date())
        val data = BuktiPanenData(
            namaPemanen = binding.etNama.text.toString().trim(),
            tanggal = tanggal,
            beratKg = String.format(Locale("id", "ID"), "%.2f", readBerat()),
            jenisLahan = getString(lahan.labelRes),
            koefisienLahan = String.format(Locale("id", "ID"), "%.1f", lahan.koefisien),
            jumlahJanjangMentah = readJanjang().coerceAtLeast(0),
            tarifPerKg = formatRupiah(UpahCalculator.TARIF_PER_KG),
            dendaPerJanjang = formatRupiah(UpahCalculator.DENDA_PER_JANJANG),
            upahKotor = formatRupiah(hasil.upahKotor),
            totalDenda = formatRupiah(hasil.denda),
            upahBersih = formatRupiah(hasil.upahBersih),
            fotoPath = fotoPanenFile?.takeIf { it.exists() }?.absolutePath,
            tandaTanganPath = signature.absolutePath
        )

        try {
            RiwayatPanenStore.save(this, data)
            Toast.makeText(this, R.string.panen_masuk_riwayat, Toast.LENGTH_LONG).show()
            setResult(RESULT_OK)
            finish()
        } catch (_: Exception) {
            Toast.makeText(this, R.string.panen_riwayat_gagal, Toast.LENGTH_LONG).show()
        }
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
            tandaTanganFile?.delete()
            val file = File(dir, "ttd_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                binding.signaturePad.toBitmap().compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            tandaTanganFile = file
            file
        } catch (e: IOException) {
            null
        }
    }

    /** Hapus file sementara; salinan permanen sudah disimpan oleh RiwayatPanenStore. */
    override fun onDestroy() {
        if (isFinishing) {
            fotoPanenFile?.delete()
            tandaTanganFile?.delete()
        }
        super.onDestroy()
    }

}
