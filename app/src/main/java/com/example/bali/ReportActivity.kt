package com.example.bali

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.FileProvider
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListAdapter
import com.google.firebase.database.*
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.*
import java.nio.file.Files
import java.nio.file.Paths

class ReportActivity : AppCompatActivity() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private val CSV_HEADER = "קוד מוצר,שם מוצר,כמות/משקל,מחיר מכירה,תאריך שינוי,עובד/ת"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "ייצוא דוחות                                                     "
        setContentView(R.layout.activity_report)

        this.filesDir
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Items")

        val ExportBtn: Button = findViewById(R.id.export_report)
        ExportBtn.setOnClickListener {
            val Items = mutableListOf<item?>()
            val filesdir = this.filesDir.toString()
            mDatabaseReference!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Items.clear()

                    val childes = snapshot.children
                    for (Ks in childes) {
                        val Amount: String? = Ks.child("ProductAmount").value as? String
                        if (Amount != "0") {
                            var BarCode = Ks.key.toString()
                            val ItemId = snapshot.child(BarCode)
                            var ProductName = ItemId.child("ProductName").value.toString()
                            var ProductAmount = ItemId.child("ProductAmount").value.toString().toInt()
                            var SalePrice = ItemId.child("SalePrice").value.toString().toInt()
                            var DateChange = ItemId.child("ChangeDate").value.toString()
                            var WorkerName = ItemId.child("WorkerName").value.toString()
                            var Item = item(BarCode,ProductName,ProductAmount,SalePrice,DateChange,WorkerName)
                            Items.add(Item)
                        }
                    }

                    var fileWriter: Writer? = null

                    try {
                        Log.d("Nooooooooooo", filesdir)
                        fileWriter = OutputStreamWriter(FileOutputStream(File(filesdir,"Items.csv")), StandardCharsets.UTF_8)

                        fileWriter.append(CSV_HEADER)
                        fileWriter.append('\n')

                        for (customer in Items) {
                            fileWriter.append(customer!!.id)
                            fileWriter.append(',')
                            fileWriter.append(customer!!.name)
                            fileWriter.append(',')
                            fileWriter.append(customer.amount.toString())
                            fileWriter.append(',')
                            fileWriter.append(customer.price.toString())
                            fileWriter.append(',')
                            fileWriter.append(customer.changedate.toString())
                            fileWriter.append(',')
                            fileWriter.append(customer.worker.toString())
                            fileWriter.append('\n')
                        }

                        Log.d("Yeeeesssssssssss","Write CSV successfully!")

                    } catch (e: Exception) {

                        e.printStackTrace()
                    } finally {
                        if (fileWriter != null) try { fileWriter.close(); } catch (e: IOException) {}
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })

            sendCsv()
        }
    }

    fun sendCsv()
    {
        val filename="Items.csv"
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
        startActivity(Intent.createChooser(emailIntent , "שלח דוח ספירת מלאי באמצעות:       "))
        Log.d("EmailSent","Write CSV successfully!")

    }
}
