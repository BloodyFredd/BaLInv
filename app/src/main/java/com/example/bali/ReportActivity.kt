package com.example.bali

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.FileProvider
import android.widget.Button
import com.google.firebase.database.*
import java.io.*
import java.nio.charset.StandardCharsets

class ReportActivity : AppCompatActivity() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private val csvHeader = "קוד מוצר,שם מוצר,כמות/משקל,מחיר מכירה,תאריך שינוי,עובד/ת"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "ייצוא דוחות                                                     "
        setContentView(R.layout.activity_report)

        this.filesDir
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Items")

        val exportBtn: Button = findViewById(R.id.export_report)
        exportBtn.setOnClickListener {
            val items = mutableListOf<item?>()
            val filesDir = this.filesDir.toString()
            mDatabaseReference!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    items.clear()

                    val childes = snapshot.children
                    for (Ks in childes) {
                        val amount: String? = Ks.child("ProductAmount").value as? String
                        if (amount != "0") {
                            var barCode = Ks.key.toString()
                            val itemId = snapshot.child(barCode)
                            var productName = itemId.child("ProductName").value.toString()
                            var productAmount = itemId.child("ProductAmount").value.toString().toInt()
                            var salePrice = itemId.child("SalePrice").value.toString().toInt()
                            var dateChange = itemId.child("ChangeDate").value.toString()
                            var workerName = itemId.child("WorkerName").value.toString()
                            var item = item(barCode,productName,productAmount,salePrice,dateChange,workerName)
                            items.add(item)
                        }
                    }

                    var fileWriter: Writer? = null

                    try {
                        fileWriter = OutputStreamWriter(FileOutputStream(File(filesDir,"Items.csv")), StandardCharsets.UTF_8)

                        fileWriter.append(csvHeader)
                        fileWriter.append('\n')

                        for (customer in items) {
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
        val fileLocation =  File(this.filesDir.absolutePath, filename)
        val path = FileProvider.getUriForFile(this, "com.example.bali.provider",fileLocation)
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

    }
}
