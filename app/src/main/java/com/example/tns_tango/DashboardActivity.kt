package com.example.tns_tango

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

/**
 * Dashboard SIPEKA (masih data contoh).
 * File: app/src/main/java/com/example/tns_tango/DashboardActivity.kt
 */
class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableSipekaEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        setupSipekaToolbar(getString(R.string.title_dashboard))
        findViewById<View>(R.id.rootDashboard).applySystemBarInsets(bottom = true)
    }
}
