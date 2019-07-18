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
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_worker.*
import java.text.SimpleDateFormat
import java.util.*

class WorkerActivity : AppCompatActivity() {

    private val TAG = "WorkerActivity"
    private var db : FirebaseFirestore? = null
    private var ProductCode: EditText? = null
    private var ProductName: EditText? = null
    private var ProductAmount: EditText? = null
    private var SalePrice: EditText? = null
    private var AddItem: Button? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mUsersDatabaseReference: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mProgressBar: ProgressDialog? = null
    private var AutoIncrement: Button? = null
    private var IncrementBtn: Button? = null
    private var DecrementBtn: Button? = null



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        supportActionBar?.title = "הוספת פריט                                                     "
        setTheme(R.style.BlueAppTheme)
        setContentView(R.layout.activity_worker)
        db = FirebaseFirestore.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Items")
        mUsersDatabaseReference = mDatabase!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()
        ProductCode = findViewById<View>(R.id.BarCode) as EditText
        ProductName = findViewById<View>(R.id.ProductName) as EditText
        ProductAmount = findViewById<View>(R.id.Amount) as EditText
        SalePrice = findViewById<View>(R.id.SalePrice) as EditText
        AddItem = findViewById<View>(R.id.add_product_button) as Button
        AutoIncrement = findViewById<View>(R.id.AutoInc) as Button
        IncrementBtn = findViewById<View>(R.id.Increment) as Button
        DecrementBtn = findViewById<View>(R.id.decrement) as Button
        mProgressBar = ProgressDialog(this)


        var ItemExternalCode: String = ""
        var MyIntent : Intent = intent
        if(MyIntent.extras!=null)
            ItemExternalCode = intent!!.getStringExtra("ItemCode")
        if(ItemExternalCode!= "")
        {
            ProductCode?.setText(ItemExternalCode)
        }


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
            scanner.setCaptureActivity(Cap::class.java)
            scanner.setOrientationLocked(true)

            scanner.initiateScan()
        }

        IncrementBtn!!.setOnClickListener{

           var temp = ProductAmount?.text.toString()
            var Amount = 0
            if(!TextUtils.isEmpty(temp))
                Amount=temp.toInt()
            Amount+=1
            ProductAmount?.setText(Amount.toString())
        }

        DecrementBtn!!.setOnClickListener{

            var temp = ProductAmount?.text.toString()
            var Amount = 0
            if(!TextUtils.isEmpty(temp))
                Amount=temp.toInt()
            if(Amount == 0)
                Toast.makeText(this, "לא ניתן להפחית!",
                    Toast.LENGTH_SHORT).show()
            if(Amount != 0)
                Amount -= 1


            ProductAmount?.setText(Amount.toString())
        }
    }


    private fun AddItemF()
    {
        val PCode = ProductCode?.text.toString()
        val PName = ProductName?.text.toString()
        val PAmount = ProductAmount?.text.toString()
        val PPrice = SalePrice?.text.toString()
        val s = SimpleDateFormat("dd/MM/yy   hh:mm")
        val format = s.format(Date())
        mProgressBar!!.setMessage("מוסיף פריט...")
        mProgressBar!!.show()
        if (!TextUtils.isEmpty(PCode) && !TextUtils.isEmpty(PName)
        && !TextUtils.isEmpty(PAmount) && !TextUtils.isEmpty(PPrice)) {

            val currentUserDb = mDatabaseReference
            mDatabaseReference!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val ItemId = snapshot.child(PCode)
                    if(ItemId.exists()){
                        mDatabaseReference!!.removeEventListener(this)
                        mProgressBar!!.dismiss()
                        Toast.makeText(this@WorkerActivity,"הפריט כבר קיים!" , Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@WorkerActivity, SearchActivity::class.java)
                        intent.putExtra("ItemCode", PCode)
                        startActivity(intent)
                    }
                    else
                    {
                        val ItemId=currentUserDb!!.child(PCode)
                        ItemId.child("ProductName").setValue(PName)
                        ItemId.child("ProductAmount").setValue(PAmount)
                        ItemId.child("SalePrice").setValue(PPrice)
                        ItemId.child("ChangeDate").setValue(format)
                        ItemId.child("Percentages").setValue("0")
                        mUsersDatabaseReference!!.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                var Username=""
                                Username = snapshot.child(mAuth!!.currentUser!!.uid).child("firstName").value.toString() + " "
                                Username += snapshot.child(mAuth!!.currentUser!!.uid).child("lastName").value.toString()
                                ItemId.child("WorkerName").setValue(Username)
                                mProgressBar!!.dismiss()
                                mDatabaseReference!!.removeEventListener(this)
                            }
                            override fun onCancelled(databaseError: DatabaseError) {}
                        })
                        ProductCode?.setText("")
                        ProductName?.setText("")
                        ProductAmount?.setText("")
                        SalePrice?.setText("")
                        Toast.makeText(this@WorkerActivity, "פריט נוסף בהצלחה!",
                            Toast.LENGTH_SHORT).show()
                        mDatabaseReference!!.removeEventListener(this)

                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}

            })
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
                    Toast.makeText(this, "בוטל", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "נסרק: " + result.contents, Toast.LENGTH_LONG).show()
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
