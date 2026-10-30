package com.example.tns_tango.feature.riwayat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tns_tango.databinding.ItemRiwayatBinding

class RiwayatAdapter(
    private val onItemClick: (RiwayatItem) -> Unit
) : ListAdapter<RiwayatItem, RiwayatAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRiwayatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class ViewHolder(
        private val binding: ItemRiwayatBinding,
        private val onItemClick: (RiwayatItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RiwayatItem) {
            binding.tvTanggal.text = item.tanggal
            binding.tvNama.text = item.namaPemanen
            binding.tvBerat.text = "${item.beratKg} kg • ${item.jenisLahan}"
            binding.tvUpah.text = item.upahBersih
            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<RiwayatItem>() {
        override fun areItemsTheSame(oldItem: RiwayatItem, newItem: RiwayatItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: RiwayatItem, newItem: RiwayatItem) = oldItem == newItem
    }
}
