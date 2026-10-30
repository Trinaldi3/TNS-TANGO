package com.example.tns_tango.feature.riwayat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tns_tango.R
import com.example.tns_tango.core.ui.applySystemBarInsets
import com.example.tns_tango.databinding.FragmentRiwayatBinding

class RiwayatFragment : Fragment() {
    private var _binding: FragmentRiwayatBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy {
        RiwayatAdapter { item ->
            startActivity(
                Intent(requireContext(), DetailPanenActivity::class.java)
                    .putExtra(DetailPanenActivity.EXTRA_RIWAYAT_ID, item.id)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRiwayatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.pageHeader.tvPageTitle.setText(R.string.title_riwayat)
        binding.pageHeader.root.applySystemBarInsets(top = true)
        binding.rvRiwayat.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRiwayat.adapter = adapter
        refreshRiwayat()
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) refreshRiwayat()
    }

    private fun refreshRiwayat() {
        val items = RiwayatPanenStore.getAll(requireContext())
        adapter.submitList(items)
        binding.tvEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        binding.rvRiwayat.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
