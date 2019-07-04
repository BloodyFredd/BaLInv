package com.example.bali

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.google.firebase.database.*

class InventoryActivity : AppCompatActivity() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var DeleteInv: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "בדיקת מלאי                                                     "
        setContentView(R.layout.activity_inventory)
        val PercentagebtnOpenActivity: Button = findViewById(R.id.AddPercentage)
        PercentagebtnOpenActivity.setOnClickListener {
            val intent = Intent(this, WorkingPercentageActivity::class.java)
            startActivity(intent)
        }

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Items")
        DeleteInv = findViewById<View>(R.id.DeleteInv) as Button
        DeleteInv!!.setOnClickListener {

            mDatabaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                val childes=snapshot.children
                for(Ks in childes) {
                    val currentChild = mDatabaseReference!!.child(Ks.key.toString())
                    Log.d("whattttttttttt?????????", Ks.key.toString())
                    currentChild.child("ProductAmount").setValue("0")

                }
            }

        })
        }

    }
}
