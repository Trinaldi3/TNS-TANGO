package com.example.tns_tango.feature.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tns_tango.R
import com.example.tns_tango.core.session.SessionManager
import com.example.tns_tango.core.ui.applySystemBarInsets
import com.example.tns_tango.databinding.FragmentDashboardBinding

/**
 * Tab 1 "Beranda": ringkasan panen (masih data contoh).
 * Tab pertama yang terbuka setelah login.
 * File: app/src/main/java/com/example/tns_tango/feature/dashboard/DashboardFragment.kt
 *
 * Alur kode:
 * onCreateView (inflate FragmentDashboardBinding) -> onViewCreated -> showGreeting()
 *   showGreeting(): SessionManager.getUsername() -> teks "Halo, <Nama>!"
 */
class DashboardFragment : Fragment() {

    // Binding hanya valid antara onCreateView dan onDestroyView
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    /** Buat tampilan dari fragment_dashboard.xml. */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    /** Header hijau sampai ke belakang status bar, lalu tampilkan sapaan. */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.headerDashboard.applySystemBarInsets(top = true)
        showGreeting()
    }

    /** Sapaan memakai username yang disimpan di SharedPreferences (huruf pertama dibesarkan). */
    private fun showGreeting() {
        val session = SessionManager(requireContext())
        val name = session.getUsername().replaceFirstChar { it.uppercase() }
        binding.tvGreeting.text = getString(R.string.dash_hello, name)
    }

    /** Lepas binding agar tidak terjadi memory leak. */
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
