package com.example.tns_tango.feature.profile

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tns_tango.R
import com.example.tns_tango.core.session.SessionManager
import com.example.tns_tango.core.ui.applySystemBarInsets
import com.example.tns_tango.databinding.FragmentProfileBinding
import com.example.tns_tango.feature.auth.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Tab 5 "Profil": profil developer (nama, hobi, kekuatan, sosial media) dan tombol Keluar.
 * File: app/src/main/java/com/example/tns_tango/feature/profile/ProfileFragment.kt
 * Isi teks ada di res/values/strings_profile.xml.
 *
 * Alur kode:
 * onViewCreated -> setupSocialLinks() -> baris sosmed diklik -> openLink() / sendEmail()
 *               -> setupLogout() -> btnLogout diklik -> showLogoutDialog() -> logout()
 *                  -> SessionManager.logout() (isLogin dihapus) -> LoginActivity
 */
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    /** Buat tampilan dari fragment_profile.xml. */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    /** Pasang header, tautan sosial media, dan tombol Keluar. */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.pageHeader.tvPageTitle.setText(R.string.title_profile)
        binding.pageHeader.root.applySystemBarInsets(top = true)

        setupSocialLinks()
        setupLogout()
    }

    /** Sambungkan tiap baris sosial media ke aksi buka tautan / kirim email. */
    private fun setupSocialLinks() {
        binding.rowGithub.setOnClickListener { openLink(getString(R.string.dev_github_url)) }
        binding.rowInstagram.setOnClickListener { openLink(getString(R.string.dev_instagram_url)) }
        binding.rowLinkedin.setOnClickListener { openLink(getString(R.string.dev_linkedin_url)) }
        binding.rowEmail.setOnClickListener { sendEmail(getString(R.string.dev_email)) }
    }

    /** Sambungkan tombol Keluar ke dialog konfirmasi. */
    private fun setupLogout() {
        binding.btnLogout.setOnClickListener { showLogoutDialog() }
    }

    /** Buka tautan di browser. Jika tidak ada aplikasi pembuka, tampilkan pesan. */
    private fun openLink(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), R.string.dev_link_error, Toast.LENGTH_SHORT).show()
        }
    }

    /** Buka aplikasi email dengan alamat tujuan terisi. */
    private fun sendEmail(address: String) {
        try {
            startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$address")))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), R.string.dev_link_error, Toast.LENGTH_SHORT).show()
        }
    }

    /** Dialog konfirmasi: Batal atau Keluar. */
    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.logout_title)
            .setMessage(R.string.logout_message)
            .setNegativeButton(R.string.logout_cancel, null)
            .setPositiveButton(R.string.logout_confirm) { _, _ -> logout() }
            .show()
    }

    /** Hapus isLogin di SharedPreferences, lalu buka Login dan kosongkan tumpukan halaman. */
    private fun logout() {
        SessionManager(requireContext()).logout()
        val intent = Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    /** Lepas binding agar tidak bocor. */
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
