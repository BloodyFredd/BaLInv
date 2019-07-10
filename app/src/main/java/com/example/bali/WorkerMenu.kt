package com.example.bali

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class WorkerMenu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = "תפריט עובד                                                     "
        setTheme(R.style.BlueAppTheme)
        setContentView(R.layout.activity_worker_menu)
        var toolbar = findViewById<View>(R.id.main_toolbar) as android.support.v7.widget.Toolbar
        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        actionbar!!.title = ""
        toolbar.overflowIcon = getDrawable(R.drawable.ic_more_vert_white_24dp)

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.logout_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.getItemId()

        if (id == R.id.logout) {
            Toast.makeText(this, "מתנתק", Toast.LENGTH_LONG).show()
            logout()
            return true
        }


        return super.onOptionsItemSelected(item)

    }

    private fun logout() {
        var Sp : SharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE)
        Sp!!.edit().putBoolean("logged",false).apply()
        var UserSp : SharedPreferences = getSharedPreferences("Mail", Context.MODE_PRIVATE)
        UserSp!!.edit().putBoolean("Manager",false).apply()
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this@WorkerMenu, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)

    }


}