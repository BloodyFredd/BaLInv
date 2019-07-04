package com.example.bali

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.change_dialog.view.*
import java.sql.Struct

class WorkingPercentageActivity : AppCompatActivity() {

    var map = mutableMapOf<String, String>()
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    //UI elements
    private var lvName: ListView? = null
    private var tvLastName: TextView? = null
    private var tvEmail: TextView? = null
    private var tvEmailVerifiied: TextView? = null
    private var mProgressBar: ProgressDialog? = null
    private val Items =  ArrayList<String?>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "רשימת פריטים                                                     "
        setContentView(R.layout.activity_working_percentage)
        initialise()
    }

    private fun initialise() {

        mAuth = FirebaseAuth.getInstance()
        lvName = findViewById<View>(R.id.ItemsList) as ListView
        mProgressBar = ProgressDialog(this)
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Items")
        //tvLastName = findViewById<View>(R.id.tv_last_name) as TextView
        // tvEmail = findViewById<View>(R.id.tv_email) as TextView
        //tvEmailVerifiied = findViewById<View>(R.id.tv_email_verifiied) as TextView
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.change_menu, menu)
    }

    override fun onStart() {
        super.onStart()
        //val mUser = mDatabaseReference!!.child("Users")//mAuth!!.currentUser
        //var muser = FirebaseAuth.getInstance().

        //val mUserReference = mDatabaseReference!!.child("Users")

        //tvEmail!!.text = mUser.email
        //tvEmailVerifiied!!.text = mUser.isEmailVerified.toString()
        //val Workers =  ArrayList<String?>()

        mProgressBar!!.setMessage("טוען פריטים...")
        mProgressBar!!.show()
        mDatabaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Items.clear()
                val childes=snapshot.children
                for(Ks in childes) {
                    val Amount: String? = Ks.child("ProductAmount").value as? String
                    if(Amount != "0") {
                        var BarCode = Ks.key.toString()
                        var name = Ks.child("ProductName").value as? String
                        name += "\nאחוז עבודה: " //+ (Ks.child("Percentages").value as? String)
                        name += Ks.child("Percentages").value as? String
                        name += "\n" + BarCode
                        Items += name
                    }
                }
                lvName!!.adapter = ArrayAdapter<String>(this@WorkingPercentageActivity, android.R.layout.simple_list_item_1, Items) as ListAdapter?
                mProgressBar!!.dismiss()

                registerForContextMenu(lvName)

            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun ChangeDialog(PCode: String)
    {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.change_dialog, null)
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

            val Percentage = mDialogView.Percentage.text.toString()
            //set the input text in TextView

            mDatabase = FirebaseDatabase.getInstance()
            mDatabaseReference = mDatabase!!.reference.child("Items")
            mDatabaseReference!!.child(PCode).child("Percentages").setValue(Percentage)
            Toast.makeText(applicationContext, "מעדכן...", Toast.LENGTH_LONG).show()
        }
        //cancel button click of custom layout
        mDialogView.dialogCancelBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
        }
    }


    override fun onContextItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.Change ->{
                val info = item.menuInfo as AdapterView.AdapterContextMenuInfo

                //var LastNL = Items[info.position]!!.lastIndexOf("\n")
                var ItemCode = Items[info.position]!!.substringAfterLast("\n")
                Log.d("whattttttttttt?????????", ItemCode)
                ChangeDialog(ItemCode)


                //val currentUserDb = mDatabaseReference!!.child(ItemCode)
                //currentUserDb.child("flag").setValue("0")
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
