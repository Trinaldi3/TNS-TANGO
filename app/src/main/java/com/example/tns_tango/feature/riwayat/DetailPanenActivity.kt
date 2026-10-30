package com.example.tns_tango.feature.riwayat

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.tns_tango.R
import com.example.tns_tango.core.ui.enableSipekaEdgeToEdge
import com.example.tns_tango.core.ui.setupSipekaToolbar
import com.example.tns_tango.databinding.ActivityDetailPanenBinding
import com.example.tns_tango.feature.panen.BuktiPanenPdf
import java.io.IOException

class DetailPanenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailPanenBinding
    private var selectedItem: RiwayatItem? = null

    private val pdfLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        val item = selectedItem ?: return@registerForActivityResult
        if (uri == null) return@registerForActivityResult
        try {
            val output = contentResolver.openOutputStream(uri)
                ?: throw IOException("Tidak dapat membuka file tujuan")
            output.use { BuktiPanenPdf.write(it, item.toBuktiPanenData()) }
            Toast.makeText(this, R.string.riwayat_pdf_success, Toast.LENGTH_LONG).show()
        } catch (_: Exception) {
            Toast.makeText(this, R.string.riwayat_pdf_failed, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableSipekaEdgeToEdge()
        binding = ActivityDetailPanenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupSipekaToolbar(getString(R.string.riwayat_detail_title))

        val id = intent.getStringExtra(EXTRA_RIWAYAT_ID)
        val item = id?.let { RiwayatPanenStore.getById(this, it) }
        if (item == null) {
            Toast.makeText(this, R.string.riwayat_detail_missing, Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        selectedItem = item
        showDetail(item)
        binding.btnUnduhPdf.setOnClickListener {
            pdfLauncher.launch("Bukti_Panen_${item.id}.pdf")
        }
    }

    private fun showDetail(item: RiwayatItem) {
        binding.tvNamaPemanen.text = item.namaPemanen
        binding.tvTanggal.text = item.tanggal
        binding.tvBerat.text = "${item.beratKg} kg"
        binding.tvJenisLahan.text = "${item.jenisLahan} (koefisien ${item.koefisienLahan})"
        binding.tvJanjang.text = "${item.jumlahJanjangMentah} janjang"
        binding.tvTarif.text = "${item.tarifPerKg} / kg"
        binding.tvUpahKotor.text = item.upahKotor
        binding.tvDenda.text = "${item.totalDenda} (${item.dendaPerJanjang} / janjang)"
        binding.tvUpahBersih.text = getString(R.string.riwayat_upah_bersih_format, item.upahBersih)

        if (!item.fotoPath.isNullOrBlank()) {
            binding.ivFotoPanen.setImageURI(Uri.fromFile(java.io.File(item.fotoPath)))
            binding.ivFotoPanen.visibility = android.view.View.VISIBLE
            binding.tvFotoKosong.visibility = android.view.View.GONE
        } else {
            binding.ivFotoPanen.visibility = android.view.View.GONE
            binding.tvFotoKosong.visibility = android.view.View.VISIBLE
        }

        val signatureFile = java.io.File(item.tandaTanganPath)
        if (signatureFile.exists()) {
            binding.ivTandaTangan.setImageURI(Uri.fromFile(signatureFile))
        }
    }

    companion object {
        const val EXTRA_RIWAYAT_ID = "extra_riwayat_panen_id"
    }
}
