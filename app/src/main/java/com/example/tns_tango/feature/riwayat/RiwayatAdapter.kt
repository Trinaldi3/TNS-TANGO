package com.example.tns_tango.feature.riwayat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tns_tango.R
import com.example.tns_tango.core.util.formatRupiah
import com.example.tns_tango.databinding.ItemRiwayatBinding

/**
 * Adapter daftar riwayat gaji (RecyclerView).
 * File: app/src/main/java/com/example/tns_tango/feature/riwayat/RiwayatAdapter.kt
 *
 * Alur kode:
 * RiwayatFragment.applyFilter() -> submitList(daftar) -> DiffUtil -> onCreateViewHolder -> onBindViewHolder -> ViewHolder.bind()
 */
class RiwayatAdapter : ListAdapter<RiwayatItem, RiwayatAdapter.ViewHolder>(DiffCallback) {

    /** Buat ViewHolder baru dari layout item_riwayat.xml. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRiwayatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    /** Isi ViewHolder dengan data item pada posisi tertentu. */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /** Pemegang tampilan satu baris riwayat. */
    class ViewHolder(private val binding: ItemRiwayatBinding) : RecyclerView.ViewHolder(binding.root) {

        /** Tampilkan tanggal, berat, upah, dan badge status (warna mengikuti makna di palet). */
        fun bind(item: RiwayatItem) {
            val context = binding.root.context
            binding.tvTanggal.text = item.tanggal
            binding.tvBerat.text = context.getString(R.string.riwayat_berat_format, item.beratKg)
            binding.tvUpah.text = formatRupiah(item.upahBersih)

            when (item.status) {
                PayStatus.TERVALIDASI -> {
                    binding.tvStatus.setText(R.string.badge_tervalidasi)
                    binding.tvStatus.setBackgroundResource(R.drawable.bg_badge_validated)
                    binding.tvStatus.setTextColor(context.getColor(R.color.sipeka_white))
                }
                PayStatus.BELUM_DIBAYAR -> {
                    binding.tvStatus.setText(R.string.badge_belum_dibayar)
                    binding.tvStatus.setBackgroundResource(R.drawable.bg_badge_unpaid)
                    binding.tvStatus.setTextColor(context.getColor(R.color.sipeka_green_700))
                }
            }
        }
    }

    /** Pembanding DiffUtil: item sama jika id sama, isi sama jika data class sama. */
    private object DiffCallback : DiffUtil.ItemCallback<RiwayatItem>() {
        override fun areItemsTheSame(oldItem: RiwayatItem, newItem: RiwayatItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: RiwayatItem, newItem: RiwayatItem): Boolean =
            oldItem == newItem
    }
}
