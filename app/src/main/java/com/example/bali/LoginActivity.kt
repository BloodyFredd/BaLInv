package com.example.bali

import android.content.Context
import android.widget.Button
import android.widget.TextView
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.app.ProgressDialog
import android.content.*
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*



class LoginActivity : AppCompatActivity() {

    //global variables
    private var email: String? = null
    private var password: String? = null
    //UI elements
    private var tvForgotPassword: TextView? = null
    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var btnLogin: Button? = null
    private var mProgressBar: ProgressDialog? = null
    private var mAuth: FirebaseAuth? = null
    var sp : SharedPreferences? = null
    var userSp : SharedPreferences? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mDatabaseReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "התחברות                                                      "

        setContentView(R.layout.activity_login)
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")

        val btnOpenActivity : Button = findViewById(R.id.sign_in)
        btnOpenActivity.setOnClickListener {
            val inten = Intent(this, WorkerMenu::class.java)
            startActivity(inten)

        }

        val managerBtnOpenActivity : Button = findViewById(R.id.Manager)
        managerBtnOpenActivity.setOnClickListener {
            val inten = Intent(this, ManagerActivity::class.java)
            startActivity(inten)

        }

        initialise()

    }

    private fun initialise() {
        tvForgotPassword = findViewById<View>(R.id.ForgotPass) as TextView
        etEmail = findViewById<View>(R.id.email) as EditText
        etPassword = findViewById<View>(R.id.password) as EditText
        btnLogin = findViewById<View>(R.id.sign_in) as Button
        mProgressBar = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()

        tvForgotPassword!!
            .setOnClickListener { startActivity(Intent(this@LoginActivity,
                ForgotPasswordActivity::class.java)) }
        btnLogin!!.setOnClickListener { loginUser() }
        sp = getSharedPreferences("login", Context.MODE_PRIVATE)
        userSp = getSharedPreferences("Mail", Context.MODE_PRIVATE)
        if(sp!!.getBoolean("logged",false))
        {
            if(userSp!!.getBoolean("Manager",false))
            {
                managerUpdateUI()
            }
            else
                updateUI()
            finish()
        }

    }

    private fun loginUser() {
        email = etEmail?.text.toString()
        password = etPassword?.text.toString()
        val mail=email
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mProgressBar!!.setMessage("מבצע התחברות...")
            mProgressBar!!.show()
            mAuth!!.signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) { task ->
                    mProgressBar!!.hide()
                    if (task.isSuccessful) {
                        mDatabaseReference!!.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                var userFlag = snapshot.child(mAuth!!.currentUser!!.uid).child("flag").value.toString()
                                if(userFlag == "1" || userFlag == "2") {
                                    if (mail == "omriavidan0402hn@gmail.com") {
                                        sp!!.edit().putBoolean("logged", true).apply()
                                        userSp!!.edit().putBoolean("Manager", true).apply()
                                        managerUpdateUI()
                                    } else {
                                        sp!!.edit().putBoolean("logged", true).apply()
                                        updateUI()
                                    }
                                    mProgressBar!!.dismiss()
                                    mDatabaseReference!!.removeEventListener(this)
                                    finish()
                                }
                                else
                                    Toast.makeText(this@LoginActivity, "1המייל ו/או הסיסמא שגויים",
                                        Toast.LENGTH_SHORT).show()

                            }
                            override fun onCancelled(databaseError: DatabaseError) {}
                        })

                    } else {
                        Toast.makeText(this@LoginActivity, "המייל ו/או הסיסמא שגויים2",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "יש להכניס את כל השדות", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        val intent = Intent(this@LoginActivity, WorkerMenu::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun managerUpdateUI() {

        val intent = Intent(this@LoginActivity, ManagerActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}