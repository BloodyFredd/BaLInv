package com.example.bali

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import android.widget.AdapterView



class ManageWorkerActivity : AppCompatActivity() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    //UI elements
    private var lvName: ListView? = null
    private var tvLastName: TextView? = null
    private var tvEmail: TextView? = null
    private var tvEmailVerifiied: TextView? = null
    private var mProgressBar: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_worker)
        val SearchbtnOpenActivity: Button = findViewById<View>(R.id.add_worker_button) as Button
        SearchbtnOpenActivity.setOnClickListener {
            val intent = Intent(this, WorkerAdd::class.java)
            startActivity(intent)
        }
        initialise()
    }

    private fun initialise() {

        mAuth = FirebaseAuth.getInstance()
        lvName = findViewById<View>(R.id.WorkersList) as ListView
        mProgressBar = ProgressDialog(this)
        //tvLastName = findViewById<View>(R.id.tv_last_name) as TextView
       // tvEmail = findViewById<View>(R.id.tv_email) as TextView
        //tvEmailVerifiied = findViewById<View>(R.id.tv_email_verifiied) as TextView
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.remove_menu, menu)
    }

    override fun onStart() {
        super.onStart()
        //val mUser = mDatabaseReference!!.child("Users")//mAuth!!.currentUser
        //var muser = FirebaseAuth.getInstance().
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        //val mUserReference = mDatabaseReference!!.child("Users")

        //tvEmail!!.text = mUser.email
        //tvEmailVerifiied!!.text = mUser.isEmailVerified.toString()
        val Workers =  ArrayList<String?>()

        mProgressBar!!.setMessage("טוען עובדים...")
        mProgressBar!!.show()
        mDatabaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val childes=snapshot.children
                for(Ks in childes) {
                    var flag: String? = Ks.child("flag").value as? String
                    if(flag == "1") {
                        var name = Ks.child("firstName").value as? String
                        name += " " + Ks.child("lastName").value as? String
                        Workers += name
                    }
                }
                lvName!!.adapter = ArrayAdapter<String>(this@ManageWorkerActivity, android.R.layout.simple_list_item_1, Workers)
                mProgressBar!!.dismiss()

                registerForContextMenu(lvName)

            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.Remove ->{
                //Log.d("hjk", R)
                Toast.makeText(applicationContext, "מוחק...", Toast.LENGTH_LONG).show()
                //val currentUserDb = mDatabaseReference!!.child(item.toString())
                //currentUserDb.child("flag").setValue("0")
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
