package com.example.tns_tango

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tns_tango.databinding.ActivityProfileDeveloperBinding

/**
 * Halaman Profil Developer (nama, hobi, kekuatan terbesar, sosial media).
 * File: app/src/main/java/com/example/tns_tango/ProfileDeveloperActivity.kt
 * Isi teks ada di res/values/strings.xml (bagian "Profil Developer").
 */
class ProfileDeveloperActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileDeveloperBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableSipekaEdgeToEdge()

        binding = ActivityProfileDeveloperBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSipekaToolbar(getString(R.string.title_profile))
        binding.rootProfile.applySystemBarInsets(bottom = true)

        binding.rowGithub.setOnClickListener { openLink(getString(R.string.dev_github_url)) }
        binding.rowInstagram.setOnClickListener { openLink(getString(R.string.dev_instagram_url)) }
        binding.rowLinkedin.setOnClickListener { openLink(getString(R.string.dev_linkedin_url)) }
        binding.rowEmail.setOnClickListener { sendEmail(getString(R.string.dev_email)) }
    }

    private fun openLink(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.dev_link_error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendEmail(address: String) {
        try {
            startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$address")))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.dev_link_error, Toast.LENGTH_SHORT).show()
        }
    }
}
