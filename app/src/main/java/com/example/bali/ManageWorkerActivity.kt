package com.example.bali

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ManageWorkerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_worker)

        val SearchbtnOpenActivity: Button = findViewById(R.id.add_worker_button)
        SearchbtnOpenActivity.setOnClickListener {
            val intent = Intent(this, WorkerAdd::class.java)
            startActivity(intent)
        }
    }
}
