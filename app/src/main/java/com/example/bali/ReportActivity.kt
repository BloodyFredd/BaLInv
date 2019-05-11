package com.example.bali

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class ReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "ייצוא דוחות                                                     "
        setContentView(R.layout.activity_report)
    }
}
