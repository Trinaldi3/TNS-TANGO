package com.example.tns_tango.feature.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tns_tango.R
import com.example.tns_tango.core.ui.applySystemBarInsets
import com.example.tns_tango.databinding.FragmentAboutBinding

/**
 * Tab 4 "Tentang": info aplikasi, fokus fitur, dan pengembang
 * (pengganti AboutActivity dari project sebelumnya).
 * File: app/src/main/java/com/example/tns_tango/feature/about/AboutFragment.kt
 *
 * Alur kode: onCreateView -> onViewCreated -> judul header + inset status bar.
 * Seluruh teks ada di res/values/strings_about.xml.
 */
class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    /** Buat tampilan dari fragment_about.xml. */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    /** Isi judul header dan atur padding status bar. */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.pageHeader.tvPageTitle.setText(R.string.title_about)
        binding.pageHeader.root.applySystemBarInsets(top = true)
    }

    /** Lepas binding agar tidak bocor. */
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
