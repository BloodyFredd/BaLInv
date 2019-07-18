package com.example.bali

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class InventoryCounts : AppCompatActivity() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    //UI elements
    private var lvName: ListView? = null
    private var mProgressBar: ProgressDialog? = null
    private val InventoryArray =  ArrayList<String?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory_counts)
        initialise()
    }

    private fun initialise() {

        mAuth = FirebaseAuth.getInstance()
        lvName = findViewById<View>(R.id.InventoryList) as ListView
        mProgressBar = ProgressDialog(this)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.remove_menu, menu)
    }

    override fun onStart() {
        super.onStart()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("InventoryCounts")


        mProgressBar!!.setMessage("טוען...")
        mProgressBar!!.show()
        mDatabaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                InventoryArray.clear()
                val childes=snapshot.children
                for(Ks in childes) {
                    var name = "ספירת מלאי לשנת "
                    name += Ks.key.toString() +": "
                    name += Ks.value.toString()
                    InventoryArray += name

                }
                lvName!!.adapter =
                    ArrayAdapter<String>(this@InventoryCounts, android.R.layout.simple_list_item_1, InventoryArray)
                mProgressBar!!.dismiss()

                registerForContextMenu(lvName)

            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}
