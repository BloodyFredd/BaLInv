package com.example.bali

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_worker.*
import kotlinx.android.synthetic.main.change_dialog.view.*
import java.text.SimpleDateFormat
import java.util.*

class SearchActivity : AppCompatActivity() {


    private val TAG = "WorkerActivity"
    private var ProductCode: EditText? = null
    private var ProductName: EditText? = null
    private var ProductAmount: EditText? = null
    private var SalePrice: EditText? = null
    private var DateChange: TextView? = null
    private var WorkerName: TextView? = null
    private var SearchItem: Button? = null
    private var EditItem: Button? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mProgressBar: ProgressDialog? = null
    private var mUsersDatabaseReference: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "חיפוש פריט                                                     "
        setTheme(R.style.PinkAppTheme)
        setContentView(R.layout.activity_search)

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Items")
        mUsersDatabaseReference = mDatabase!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()
        ProductCode = findViewById<View>(R.id.BarCode) as EditText
        ProductName = findViewById<View>(R.id.ProductName) as EditText
        ProductAmount = findViewById<View>(R.id.Amount) as EditText
        DateChange = findViewById<View>(R.id.DateChange) as TextView
        SalePrice = findViewById<View>(R.id.SalePrice) as EditText
        SearchItem = findViewById<View>(R.id.search_product_button) as Button
        EditItem = findViewById<View>(R.id.Edit_product_button) as Button
        WorkerName = findViewById<View>(R.id.WorkerName) as TextView
        mProgressBar = ProgressDialog(this)

        var ItemExternalCode: String = ""
        var MyIntent : Intent = intent
        if(MyIntent.extras!=null)
            ItemExternalCode = intent!!.getStringExtra("ItemCode")
        if(ItemExternalCode!= "")
        {
            SearchItem(ItemExternalCode)
        }
        SearchItem!!.setOnClickListener {
            SearchItem("")
        }

        EditItem!!.setOnClickListener {
            showDialog()
        }
        Scanner.setOnClickListener {
            val scanner = IntentIntegrator(this)
            scanner.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES)
            scanner.setBeepEnabled(false)
            scanner.setCaptureActivity(Cap::class.java)
            scanner.setOrientationLocked(true)

            scanner.initiateScan()
        }
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


    fun ChangeDialog(PCode: String)
    {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.confirmation_deleteworker, null)
        //AlertDialogBuilder
        val mBuilder = android.app.AlertDialog.Builder(this)
            .setView(mDialogView)
        //show dialog
        val  mAlertDialog = mBuilder.show()
        var ConfTextView: TextView = mAlertDialog.findViewById<View>(R.id.Confirmation) as TextView
        ConfTextView!!.setText("הפריט לא קיים,האם תרצה לעבור לחלון הוספת פריט?")
        //login button click of custom layout
        mDialogView.dialogYesBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
            //get text from EditTexts of custom layout
            val intent = Intent(this@SearchActivity, WorkerActivity::class.java)
            intent.putExtra("ItemCode", PCode)
            startActivity(intent)
        }
        //cancel button click of custom layout
        mDialogView.dialogCancelBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
        }
    }

    private fun SearchItem(ItemCode : String) {
        var PCode = ProductCode?.text.toString()
        if(ItemCode != "") {
            PCode = ItemCode
            ProductCode?.setText(ItemCode)
        }
        if (!TextUtils.isEmpty(PCode)) {
            mProgressBar!!.setMessage("טוען מוצרים...")
            mProgressBar!!.show()
            mDatabaseReference!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val ItemId = snapshot.child(PCode)
                    if(ItemId.exists()){
                        ProductName?.setText(ItemId.child("ProductName").value.toString())
                        ProductAmount?.setText(ItemId.child("ProductAmount").value.toString())
                        SalePrice?.setText(ItemId.child("SalePrice").value.toString())
                        DateChange?.setText("תאריך שינוי:  " + ItemId.child("ChangeDate").value.toString())
                        WorkerName?.setText("שם העובד/ת:  " + ItemId.child("WorkerName").value.toString())
                        mProgressBar!!.dismiss()
                        mDatabaseReference!!.removeEventListener(this)
                    }
                    else
                    {
                        mDatabaseReference!!.removeEventListener(this)
                        mProgressBar!!.dismiss()
                        Toast.makeText(this@SearchActivity,"הפריט לא קיים!" , Toast.LENGTH_SHORT).show()
                        ChangeDialog(PCode)
                    }
                }


                override fun onCancelled(databaseError: DatabaseError) {}
            })

        } else {
            Toast.makeText(this, "אנא וודא/י שכל השדות הוזנו!", Toast.LENGTH_SHORT).show()

        }


    }

    private fun AddItemFun() {
        val PCode = ProductCode?.text.toString()
        val PName = ProductName?.text.toString()
        val PAmount = ProductAmount?.text.toString()
        val PPrice = SalePrice?.text.toString()
        val s = SimpleDateFormat("dd/MM/yy   hh:mm")
        val format = s.format(Date())
        mProgressBar!!.setMessage("מעדכן פריט...")
        mProgressBar!!.show()
        if (!TextUtils.isEmpty(PCode) && !TextUtils.isEmpty(PName)
            && !TextUtils.isEmpty(PAmount) && !TextUtils.isEmpty(PPrice)) {

            val currentUserDb = mDatabaseReference
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
                    mUsersDatabaseReference!!.removeEventListener(this)
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })
            ProductCode?.setText("")
            ProductName?.setText(null)
            ProductAmount?.setText(null)
            SalePrice?.setText(null)
            DateChange?.setText("תאריך שינוי:")
            WorkerName?.setText("שם העובד/ת:")

            Toast.makeText(
                this, "פריט עודכן בהצלחה!",
                Toast.LENGTH_SHORT
            ).show()


        } else {
            Toast.makeText(
                this, "אנא וודא/י שכל השדות הוזנו!",
                Toast.LENGTH_SHORT
            ).show()
        }

        mProgressBar!!.hide()
        mProgressBar!!.dismiss()
    }

    private fun showDialog() {
        // Late initialize an alert dialog object
        lateinit var dialog: AlertDialog


        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(this)

        // Set a title for alert dialog
        builder.setTitle("שינוי פריט                                         ")

        // Set a message for alert dialog
        builder.setMessage("את/ה עומד/ת לשנות ערך של פריט, האם ברצונך להמשיך?")


        // On click listener for dialog buttons
        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> AddItemFun()
                DialogInterface.BUTTON_NEGATIVE -> toast("לא בוצע עידכון")

            }
        }


        // Set the alert dialog positive/yes button
        builder.setPositiveButton("כן", dialogClickListener)

        // Set the alert dialog negative/no button
        builder.setNegativeButton("לא", dialogClickListener)



        // Initialize the AlertDialog using builder object
        dialog = builder.create()

        // Finally, display the alert dialog
        dialog.show()
    }

    fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}
