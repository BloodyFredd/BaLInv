package com.example.bali

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ManagerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle("תפריט מנהל")
        setContentView(R.layout.activity_manager)

        val SearchbtnOpenActivity: Button = findViewById(R.id.search_product)
        SearchbtnOpenActivity.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        val AddingbtnOpenActivity: Button = findViewById(R.id.add_product_button)
        AddingbtnOpenActivity.setOnClickListener {
            val intent = Intent(this, WorkerActivity::class.java)
            startActivity(intent)
        }


        val WorkersingbtnOpenActivity: Button = findViewById(R.id.add_worker_button)
        WorkersingbtnOpenActivity.setOnClickListener {
            val intent = Intent(this, ManageWorkerActivity::class.java)
            startActivity(intent)
        }


        val ReportsbtnOpenActivity: Button = findViewById(R.id.reports_button)
        ReportsbtnOpenActivity.setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java)
            startActivity(intent)
        }
    }


}
