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
    private var DeleteInv: Button? = null
    private var FinishCountBtn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "בדיקת מלאי                                                     "
        setContentView(R.layout.activity_inventory)
        val PercentagebtnOpenActivity: Button = findViewById(R.id.AddPercentage)
        PercentagebtnOpenActivity.setOnClickListener {
            val intent = Intent(this, WorkingPercentageActivity::class.java)
            startActivity(intent)
        }
        val InventoryCountsBtn: Button = findViewById(R.id.InventoryCounts)
        InventoryCountsBtn.setOnClickListener {
            val intent = Intent(this, InventoryCounts::class.java)
            startActivity(intent)
        }
        FinishCountBtn = findViewById<View>(R.id.FinishCount) as Button
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Items")
        DeleteInv = findViewById<View>(R.id.DeleteInv) as Button
        DeleteInv!!.setOnClickListener {
            ConfirmationDialog("Delete")

        }
        FinishCountBtn!!.setOnClickListener {

            ConfirmationDialog("Finish")
        }

    }

    fun ConfirmationDialog(PCode: String): Int {
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

            val Code = mDialogView.Percentage.text.toString()
            //set the input text in TextView
            if(Code == "nisim0309") {
                Toast.makeText(applicationContext, "מבצע...", Toast.LENGTH_LONG).show()
                if(PCode == "Delete")
                    DeleteInvertory()
                if(PCode == "Finish")
                    FinishCount()
            }
        }
        //cancel button click of custom layout
        mDialogView.dialogCancelBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
        }
        return 1
    }

    fun DeleteInvertory()
    {
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Items")
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

    fun FinishCount()
    {
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Items")
        var Sum = 0.0
        mDatabaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                val childes = snapshot.children
                var PercentageAmount = 0.0
                var ProductAmount = 0.0
                var ProductBuyingPrice = 0.0
                for (Ks in childes) {
                    //val currentChild = mDatabaseReference!!.child(Ks.key.toString())
                    PercentageAmount = (Ks.child("Percentages").value as? String)!!.toDouble()
                    ProductAmount = (Ks.child("ProductAmount").value as? String)!!.toDouble()
                    ProductBuyingPrice = (Ks.child("SalePrice").value as? String)!!.toDouble()

                    if(PercentageAmount != 0.0 && ProductAmount != 0.0) {
                        Log.d("whattttttttttt?????????", PercentageAmount.toString() )
                        PercentageAmount = ((100-PercentageAmount)/100)
                        ProductBuyingPrice = (ProductBuyingPrice/1.17)*PercentageAmount
                        Sum += ProductAmount*ProductBuyingPrice
                    }
                    PercentageAmount= 0.0
                    ProductAmount = 0.0
                    ProductBuyingPrice = 0.0

                }
                Log.d("whattttttttttt?????????", Sum.toString() )
                mDatabaseReferenceInventory = mDatabase!!.reference.child("InventoryCounts")
                val s = SimpleDateFormat("yyyy")
                val format = s.format(Date())

                mDatabaseReferenceInventory!!.child(format).setValue(Sum)
                Sum = 0.0
            }
        })
    }
}
