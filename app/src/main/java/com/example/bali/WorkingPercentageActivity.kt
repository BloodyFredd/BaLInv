package com.example.bali

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.change_dialog.view.*

class WorkingPercentageActivity : AppCompatActivity() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    //UI elements
    private var lvName: ListView? = null
    private var mProgressBar: ProgressDialog? = null
    private val items =  ArrayList<String?>()
    private var nShown: CheckBox? = null

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
        nShown = findViewById<View>(R.id.CheckB) as CheckBox
        nShown!!.setOnClickListener{
            loadItems()
        }

    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.change_menu, menu)
    }

    override fun onStart() {
        super.onStart()
        loadItems()
    }

    fun loadItems(){
        mProgressBar!!.setMessage("טוען פריטים...")
        mProgressBar!!.show()
        mDatabaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                items.clear()
                val childes=snapshot.children
                for(Ks in childes) {
                    val amount: String? = Ks.child("ProductAmount").value as? String
                    if(!nShown!!.isChecked){
                        if(amount != "0") {

                            var barCode = Ks.key.toString()
                            var name = Ks.child("ProductName").value as? String
                            name += "\nאחוז עבודה: " //+ (Ks.child("Percentages").value as? String)
                            name += Ks.child("Percentages").value as? String
                            name += "\n" + barCode
                            items += name
                        }
                    }
                    else
                    {
                        if(Ks.child("Percentages").value as? String =="0" && amount != "0")
                        {
                            var barCode = Ks.key.toString()
                            var name = Ks.child("ProductName").value as? String
                            name += "\nאחוז עבודה: " //+ (Ks.child("Percentages").value as? String)
                            name += Ks.child("Percentages").value as? String
                            name += "\n" + barCode
                            items += name
                        }
                    }
                }
                lvName!!.adapter = ArrayAdapter<String>(this@WorkingPercentageActivity, android.R.layout.simple_list_item_1, items) as ListAdapter?
                mProgressBar!!.dismiss()

                registerForContextMenu(lvName)

            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun changeDialog(PCode: String)
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

            val percentage = mDialogView.Percentage.text.toString()
            //set the input text in TextView

            mDatabase = FirebaseDatabase.getInstance()
            mDatabaseReference = mDatabase!!.reference.child("Items")
            mDatabaseReference!!.child(PCode).child("Percentages").setValue(percentage)
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
            R.id.ChangeMenu ->{
                val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
                var itemCode = items[info.position]!!.substringAfterLast("\n")
                changeDialog(itemCode)

                return true
            }
            R.id.EditDetailsProduct ->{
                val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
                var itemCode = items[info.position]!!.substringAfterLast("\n")
                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra("ItemCode", itemCode)
                startActivity(intent)

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
