package com.example.bali

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_manager.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_search.main_toolbar
import kotlinx.android.synthetic.main.change_dialog.view.*
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.*
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class ManagerActivity : AppCompatActivity() {

    //private var toolbar: android.support.v7.widget.Toolbar? = null
    private val CSV_HEADER = "קוד מוצר,שם מוצר,כמות/משקל,מחיר מכירה,תאריך שינוי,עובד/ת"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "תפריט מנהל                                                     "


        setContentView(R.layout.activity_manager)
        var toolbar = findViewById<View>(R.id.main_toolbar) as android.support.v7.widget.Toolbar
        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        actionbar!!.title = ""
        toolbar.overflowIcon = getDrawable(R.drawable.ic_more_vert_white_24dp)
        File(this.filesDir,"customer.csv")
        val SearchbtnOpenActivity: Button = findViewById(R.id.search_product)
        SearchbtnOpenActivity.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
        ///writeCsv()
        //sendCsv()
        val AddingbtnOpenActivity: Button = findViewById(R.id.add_product_button)
        AddingbtnOpenActivity.setOnClickListener {
            val intent = Intent(this, WorkerActivity::class.java)
            startActivity(intent)
        }


        val WorkersingbtnOpenActivity: Button = findViewById(R.id.add_worker_button)
        WorkersingbtnOpenActivity.setOnClickListener {
            val intent = Intent(this, ManageWorkerActivity::class.java)
            startActivity(intent)
        }


        val ReportsbtnOpenActivity: Button = findViewById(R.id.reports_button)
        ReportsbtnOpenActivity.setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java)
            startActivity(intent)
        }

        val InventorybtnOpenActivity: Button = findViewById(R.id.inventory_button)
        InventorybtnOpenActivity.setOnClickListener {
            val intent = Intent(this, InventoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.logout_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.getItemId()

        if (id == R.id.logout) {

            ConfirmationDialog()
            return true
        }


        return super.onOptionsItemSelected(item)

    }


    fun ConfirmationDialog(): Int {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.confirmation_deleteworker, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
        //show dialog

        val  mAlertDialog = mBuilder.show()
        var ConfTextView: TextView = mAlertDialog.findViewById<View>(R.id.Confirmation) as TextView
        ConfTextView!!.setText("האם אתה בטוח שברצונך להתנתק?")

        //login button click of custom layout
        mDialogView.dialogYesBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
            Toast.makeText(this, "מתנתק...", Toast.LENGTH_LONG).show()
            logout()
            //get text from EditTexts of custom layout


        }
        //cancel button click of custom layout
        mDialogView.dialogCancelBtn.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()


        }
        return 1
    }

    private fun logout() {
        var Sp : SharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE)
        Sp!!.edit().putBoolean("logged",false).apply()
        var UserSp : SharedPreferences = getSharedPreferences("Mail", Context.MODE_PRIVATE)
        UserSp!!.edit().putBoolean("Manager",false).apply()
        FirebaseAuth.getInstance().signOut()
        finish()
        val intent = Intent(this@ManagerActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)

    }

    /*fun writeCsv()
    {
        val customers = Arrays.asList(
            item("1", "Jack mith", "Massachusetts", 23)
         )

        var fileWriter: Writer? = null

        try {
            Log.d("Nooooooooooo",this.filesDir.toString())
            fileWriter =OutputStreamWriter(FileOutputStream(File(this.filesDir,"customer.csv")),StandardCharsets.UTF_8)

            fileWriter.append(CSV_HEADER)
            fileWriter.append('\n')

            for (customer in customers) {
                fileWriter.append(customer.id)
                fileWriter.append(',')
                fileWriter.append(customer.name)
                fileWriter.append(',')
                fileWriter.append(customer.address)
                fileWriter.append(',')
                fileWriter.append(customer.age.toString())
                fileWriter.append('\n')
            }

            Log.d("Yeeeesssssssssss","Write CSV successfully!")

        } catch (e: Exception) {

            e.printStackTrace()
        } finally {
            if (fileWriter != null) try { fileWriter.close(); } catch (e: IOException ) {}
        }

    }*/
    /*fun sendCsv()
    {
        val filename="customer.csv"
        val filelocation =  File(this.filesDir.absolutePath, filename)
        val path = FileProvider.getUriForFile(this, "com.example.bali.provider",filelocation)
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        // set the type to 'email'
        emailIntent .setType("vnd.android.cursor.dir/email")
        val to = arrayOf("omriavidan0402hn@gmail.com")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to)
        // the attachment
        emailIntent .putExtra(Intent.EXTRA_STREAM, path)
        // the mail subject
        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Subject")
        startActivity(Intent.createChooser(emailIntent , "Send email..."))
        Log.d("EmailSent","Write CSV successfully!")

    }*/




}
