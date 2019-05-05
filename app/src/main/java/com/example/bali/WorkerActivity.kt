package com.example.bali

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
//import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_worker.*
import org.w3c.dom.Document

class WorkerActivity : AppCompatActivity() {

    private val TAG = "WorkerActivity"
    private var db : FirebaseFirestore? = null
    private var ProductCode: EditText? = null
    private var ProductName: EditText? = null
    private var ProductAmount: EditText? = null
    private var SalePrice: EditText? = null
    private var AddItem: Button? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mProgressBar: ProgressDialog? = null
    private var AutoIncrement: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker)
        //val Scanner : Button = findViewById(R.id.sign_in)
        db = FirebaseFirestore.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Items")
        ProductCode = findViewById<View>(R.id.BarCode) as EditText
        ProductName = findViewById<View>(R.id.ProductName) as EditText
        ProductAmount = findViewById<View>(R.id.Amount) as EditText
        SalePrice = findViewById<View>(R.id.SalePrice) as EditText
        AddItem = findViewById<View>(R.id.add_product_button) as Button
        AutoIncrement = findViewById<View>(R.id.AutoInc) as Button
        mProgressBar = ProgressDialog(this)
        AddItem!!.setOnClickListener{
            AddItemF()
        }

        AutoIncrement!!.setOnClickListener{
            IncrementVal()
        }

        Scanner.setOnClickListener {
            val scanner = IntentIntegrator(this)
            scanner.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES)
            scanner.setBeepEnabled(false)
            scanner.setCaptureActivity(Cap::class.java);
            scanner.setOrientationLocked(true)

            scanner.initiateScan()
        }
    }


    private fun AddItemF()
    {
        val PCode = ProductCode?.text.toString()
        val PName = ProductName?.text.toString()
        val PAmount = ProductAmount?.text.toString()
        val PPrice = SalePrice?.text.toString()
        mProgressBar!!.setMessage("מוסיף פריט...")
        mProgressBar!!.show()
        if (!TextUtils.isEmpty(PCode) && !TextUtils.isEmpty(PName)
        && !TextUtils.isEmpty(PAmount) && !TextUtils.isEmpty(PPrice)) {

            val currentUserDb = mDatabaseReference
            var ItemId=currentUserDb!!.child(PCode)
            ItemId.child("ProductName").setValue(PName)
            ItemId.child("ProductAmount").setValue(PAmount)
            ItemId.child("SalePrice").setValue(PPrice)
            ProductCode?.setText("")
            ProductName?.setText("")
            ProductAmount?.setText("")
            SalePrice?.setText("")
            Toast.makeText(this, "פריט נוסף בהצלחה!",
                Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(
                this, "אנא וודא/י שכל השדות הוזנו!",
                Toast.LENGTH_SHORT
            ).show()
        }

        mProgressBar!!.hide()
        mProgressBar!!.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
                    ProductCode!!.setText(result.contents)
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun IncrementVal() {


        val sfDocRef = db!!.collection("BarCodes").document("BarC")
        var newC=""
        db!!.runTransaction() { transaction ->
            val snapshot = transaction.get(sfDocRef)

            // Note: this could be done without a transaction
            //       by updating the population using FieldValue.increment()
            val newCode = "A"+((snapshot.getString("Code")!!.substring(1)).toInt()+1).toString()
            transaction.update(sfDocRef, "Code", newCode)
            newC=newCode
            // Success
            null
        }.addOnSuccessListener {
            ProductCode!!.setText(newC)


        }
            .addOnFailureListener { e -> Log.w(TAG, "Transaction failure.", e) }


    }




}
