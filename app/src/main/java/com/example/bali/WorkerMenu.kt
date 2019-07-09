package com.example.bali

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class WorkerMenu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = "תפריט עובד                                                     "
        setTheme(R.style.BlueAppTheme)
        setContentView(R.layout.activity_worker_menu)

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
    }

}