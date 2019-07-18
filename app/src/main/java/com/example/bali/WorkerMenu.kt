package com.example.bali

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.change_dialog.view.*

class WorkerMenu : AppCompatActivity() {


    private var mDatabase: FirebaseDatabase? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null

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

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()

        val searchbtnOpenActivity: Button = findViewById(R.id.search_product)
        searchbtnOpenActivity.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        val addingbtnOpenActivity: Button = findViewById(R.id.add_product_button)
        addingbtnOpenActivity.setOnClickListener {
            val intent = Intent(this, WorkerActivity::class.java)
            startActivity(intent)
        }

        mDatabaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var userflag = snapshot.child(mAuth!!.currentUser!!.uid).child("flag").value.toString()
                if(userflag == "0") {
                    mDatabaseReference!!.removeEventListener(this)
                    logout()

                    finish()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.logout_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.itemId

        if (id == R.id.logout) {
            confirmationDialog()
            return true
        }


        return super.onOptionsItemSelected(item)

    }

    private fun confirmationDialog(): Int {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.confirmation_deleteworker, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
        //show dialog

        val  mAlertDialog = mBuilder.show()
        var confTextView: TextView = mAlertDialog.findViewById<View>(R.id.Confirmation) as TextView
        confTextView!!.text = "האם אתה בטוח שברצונך להתנתק?"

        //login button click of custom layout
        mDialogView.dialogYesBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
            Toast.makeText(this, "מתנתק...", Toast.LENGTH_LONG).show()
            logout()
            //get text from EditTexts of custom layout


        }
        //cancel button click of custom layout
        mDialogView.dialogCancelBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()


        }
        return 1
    }

    private fun logout() {
        var sp : SharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE)
        sp!!.edit().putBoolean("logged",false).apply()
        var userSp : SharedPreferences = getSharedPreferences("Mail", Context.MODE_PRIVATE)
        userSp!!.edit().putBoolean("Manager",false).apply()
        FirebaseAuth.getInstance().signOut()
        finish()
        val intent = Intent(this@WorkerMenu, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)

    }


}