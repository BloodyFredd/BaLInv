package com.example.bali

import android.app.AlertDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.change_dialog.view.*
import java.text.SimpleDateFormat
import java.util.*

class InventoryActivity : AppCompatActivity() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabaseReferenceInventory: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var deleteInv: Button? = null
    private var finishCountBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "בדיקת מלאי                                                     "
        setContentView(R.layout.activity_inventory)
        val percentageBtnOpenActivity: Button = findViewById(R.id.AddPercentage)
        percentageBtnOpenActivity.setOnClickListener {
            val intent = Intent(this, WorkingPercentageActivity::class.java)
            startActivity(intent)
        }
        val inventoryCountsBtn: Button = findViewById(R.id.InventoryCounts)
        inventoryCountsBtn.setOnClickListener {
            val intent = Intent(this, InventoryCounts::class.java)
            startActivity(intent)
        }
        finishCountBtn = findViewById<View>(R.id.FinishCount) as Button
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Items")
        deleteInv = findViewById<View>(R.id.DeleteInv) as Button
        deleteInv!!.setOnClickListener {
            confirmationDialog("Delete")

        }
        finishCountBtn!!.setOnClickListener {

            confirmationDialog("Finish")
        }

    }

    private fun confirmationDialog(PCode: String): Int {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.confirmation_dialog, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
        //show dialog
        val  mAlertDialog = mBuilder.show()
        //login button click of custom layout
        mDialogView.dialogYesBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
            //get text from EditTexts of custom layout

            val code = mDialogView.Percentage.text.toString()
            //set the input text in TextView
            if(code == "nisim0309") {
                Toast.makeText(applicationContext, "מבצע...", Toast.LENGTH_LONG).show()
                if(PCode == "Delete")
                    deleteInventory()
                if(PCode == "Finish")
                    finishCount()
            }
        }
        //cancel button click of custom layout
        mDialogView.dialogCancelBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
        }
        return 1
    }

    private fun deleteInventory()
    {
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Items")
        mDatabaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                val childes=snapshot.children
                for(Ks in childes) {
                    val currentChild = mDatabaseReference!!.child(Ks.key.toString())
                    currentChild.child("ProductAmount").setValue("0")

                }
                mDatabaseReference!!.removeEventListener(this)
            }

        })
    }

    fun finishCount()
    {
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Items")
        var sum = 0.0
        mDatabaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                val childes = snapshot.children
                var percentageAmount = 0.0
                var productAmount = 0.0
                var productBuyingPrice = 0.0
                for (Ks in childes) {
                    //val currentChild = mDatabaseReference!!.child(Ks.key.toString())
                    percentageAmount = (Ks.child("Percentages").value as? String)!!.toDouble()
                    productAmount = (Ks.child("ProductAmount").value as? String)!!.toDouble()
                    productBuyingPrice = (Ks.child("SalePrice").value as? String)!!.toDouble()

                    if(percentageAmount != 0.0 && productAmount != 0.0) {
                        percentageAmount = ((100-percentageAmount)/100)
                        productBuyingPrice = (productBuyingPrice/1.17)*percentageAmount
                        sum += productAmount*productBuyingPrice
                    }
                    percentageAmount= 0.0
                    productAmount = 0.0
                    productBuyingPrice = 0.0

                }
                mDatabaseReferenceInventory = mDatabase!!.reference.child("InventoryCounts")
                val s = SimpleDateFormat("yyyy")
                val format = s.format(Date())

                mDatabaseReferenceInventory!!.child(format).setValue(sum)
                sum = 0.0
                mDatabaseReference!!.removeEventListener(this)
            }
        })

    }
}
