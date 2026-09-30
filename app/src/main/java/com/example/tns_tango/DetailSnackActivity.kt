package com.example.tns_tango

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tns_tango.databinding.ActivityDetailSnackBinding

class DetailSnackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailSnackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailSnackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = getString(R.string.snack_name)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}