package com.example.tns_tango.feature.riwayat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tns_tango.R
import com.example.tns_tango.core.ui.applySystemBarInsets
import com.example.tns_tango.databinding.FragmentRiwayatBinding

/**
 * Tab 2 "Riwayat": daftar riwayat gaji dengan filter status (data contoh).
 * File: app/src/main/java/com/example/tns_tango/feature/riwayat/RiwayatFragment.kt
 *
 * Alur kode:
 * onViewCreated -> setupList() -> setupFilter()
 *   chip filter diklik -> applyFilter(status) -> RiwayatAdapter.submitList() -> daftar ter-update
 */
class RiwayatFragment : Fragment() {

    private var _binding: FragmentRiwayatBinding? = null
    private val binding get() = _binding!!

    private val adapter = RiwayatAdapter()

    /** Semua data riwayat; filter hanya memilih sebagian dari daftar ini. */
    private val allItems = RiwayatSampleData.items()

    /** Buat tampilan dari fragment_riwayat.xml. */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRiwayatBinding.inflate(inflater, container, false)
        return binding.root
    }

    /** Pasang header, daftar, dan filter. */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.pageHeader.tvPageTitle.setText(R.string.title_riwayat)
        binding.pageHeader.root.applySystemBarInsets(top = true)

        setupList()
        setupFilter()
    }

    /** Pasang RecyclerView (LayoutManager + Adapter) dan isi dengan semua data. */
    private fun setupList() {
        binding.rvRiwayat.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRiwayat.adapter = adapter
        applyFilter(null)
    }

    /** Sambungkan chip Semua / Tervalidasi / Belum dibayar ke applyFilter(). */
    private fun setupFilter() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            when (checkedIds.firstOrNull()) {
                R.id.chipTervalidasi -> applyFilter(PayStatus.TERVALIDASI)
                R.id.chipBelumDibayar -> applyFilter(PayStatus.BELUM_DIBAYAR)
                else -> applyFilter(null)
            }
        }
    }

    /**
     * Tampilkan data sesuai status. null = semua data.
     * Jika hasil kosong, tampilkan teks "belum ada data".
     */
    private fun applyFilter(status: PayStatus?) {
        val filtered = if (status == null) allItems else allItems.filter { it.status == status }
        adapter.submitList(filtered)
        binding.tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    /** Lepas binding dan adapter RecyclerView agar tidak bocor. */
    override fun onDestroyView() {
        binding.rvRiwayat.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
