package com.example.bali

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import android.widget.AdapterView
import android.widget.AdapterView.AdapterContextMenuInfo
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.change_dialog.view.*
import kotlinx.android.synthetic.main.change_dialog.view.dialogCancelBtn
import kotlinx.android.synthetic.main.change_dialog.view.dialogYesBtn
import kotlinx.android.synthetic.main.edit_worker_details.view.*


class ManageWorkerActivity : AppCompatActivity() {
    var map = mutableMapOf<String, String>()
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null
    //UI elements
    private var lvName: ListView? = null
    private var tvEmail: TextView? = null
    private var tvEmailVerifiied: TextView? = null
    private var mProgressBar: ProgressDialog? = null
    private val Workers =  ArrayList<String?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "רשימת עובדים                                                     "
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
        mDatabaseReference = mDatabase!!.reference.child("Users")
        //val mUserReference = mDatabaseReference!!.child("Users")

        //tvEmail!!.text = mUser.email
        //tvEmailVerifiied!!.text = mUser.isEmailVerified.toString()
        //val Workers =  ArrayList<String?>()

        mProgressBar!!.setMessage("טוען עובדים...")
        mProgressBar!!.show()
        mDatabaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Workers.clear()
                val childes=snapshot.children
                for(Ks in childes) {
                    val flag: String? = Ks.child("flag").value as? String
                    var UserCode=Ks.key.toString()

                    if(flag == "1") {
                        var name = Ks.child("firstName").value as? String
                        name += " " + Ks.child("lastName").value as? String
                        Workers += name
                        map[name.toString()] = UserCode
                    }
                }
                lvName!!.adapter =
                    ArrayAdapter<String>(this@ManageWorkerActivity, android.R.layout.simple_list_item_1, Workers) as ListAdapter?
                mProgressBar!!.dismiss()

                registerForContextMenu(lvName)

            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun ConfirmationDialog(UserCode: String): Int {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.confirmation_deleteworker, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
        //show dialog

        val  mAlertDialog = mBuilder.show()
        var ConfTextView: TextView = mAlertDialog.findViewById<View>(R.id.Confirmation) as TextView
        ConfTextView!!.setText("האם אתה בטוח שברצונך למחוק עובד זה?")

        //login button click of custom layout
        mDialogView.dialogYesBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
            DeleteWorker(UserCode)
            //get text from EditTexts of custom layout


        }
        //cancel button click of custom layout
        mDialogView.dialogCancelBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()


        }
        return 1
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.RemoveWorker ->{
                val info = item.menuInfo as AdapterContextMenuInfo
                //Log.d("whattttttttttt?????????", map.get(Workers[info.position]))
                var UserC = map.get(Workers[info.position]).toString()
                ConfirmationDialog(UserC)
                return true
            }
            R.id.EditWorker ->{
                val info = item.menuInfo as AdapterContextMenuInfo
                var UserC = map.get(Workers[info.position]).toString()
                ConfirmationDialogEditDetails(UserC)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun DeleteWorker(UserCode : String)
    {
        Toast.makeText(applicationContext, "מוחק...", Toast.LENGTH_LONG).show()
        val currentUserDb = mDatabaseReference!!.child(UserCode)
        currentUserDb.child("flag").setValue("0")
    }

    fun ConfirmationDialogEditDetails(UserCode: String): Int {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.edit_worker_details, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
        //show dialog
        val  mAlertDialog = mBuilder.show()
        mDatabaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var firstname = snapshot.child(UserCode).child("firstName").value.toString()
                var lastname = snapshot.child(UserCode).child("lastName").value.toString()
                mDialogView.FirstName.setText(firstname)
                mDialogView.LastName.setText(lastname)
                mDatabaseReference!!.removeEventListener(this)
            }


            override fun onCancelled(databaseError: DatabaseError) {}
        })
        //login button click of custom layout
        mDialogView.dialogYesBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
            //get text from EditTexts of custom layout

            val firstName = mDialogView.FirstName.text.toString()
            val lastName = mDialogView.LastName.text.toString()
            Toast.makeText(applicationContext, "מעדכן...", Toast.LENGTH_LONG).show()
            val currentUserDb = mDatabaseReference!!.child(UserCode)
            if(!TextUtils.isEmpty(firstName))
                currentUserDb.child("firstName").setValue(firstName)
            if(!TextUtils.isEmpty(lastName))
                currentUserDb.child("lastName").setValue(lastName)

        }
        //cancel button click of custom layout
        mDialogView.dialogCancelBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
        }
        return 1
    }
}
