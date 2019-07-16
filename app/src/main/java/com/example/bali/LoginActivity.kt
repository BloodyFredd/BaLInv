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

    private val TAG = "LoginActivity"
    //global variables
    private var email: String? = null
    private var password: String? = null
    //UI elements
    private var tvForgotPassword: TextView? = null
    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var btnLogin: Button? = null
    private var btnCreateAccount: Button? = null
    private var mProgressBar: ProgressDialog? = null
    //Firebase references
    private var mAuth: FirebaseAuth? = null
    var Sp : SharedPreferences? = null
    var UserSp : SharedPreferences? = null
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

        val ManagerbtnOpenActivity : Button = findViewById(R.id.Manager)
        ManagerbtnOpenActivity.setOnClickListener {
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
        //btnCreateAccount = findViewById<View>(R.id.btn_register_account) as Button
        mProgressBar = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()

        tvForgotPassword!!
            .setOnClickListener { startActivity(Intent(this@LoginActivity,
                ForgotPasswordActivity::class.java)) }
        //btnCreateAccount!!
        //    .setOnClickListener { startActivity(Intent(this@LoginActivity,
        //        CreateAccountActivity::class.java)) }
        btnLogin!!.setOnClickListener { loginUser() }
        Sp = getSharedPreferences("login", Context.MODE_PRIVATE)
        UserSp = getSharedPreferences("Mail", Context.MODE_PRIVATE)
        if(Sp!!.getBoolean("logged",false))
        {
            if(UserSp!!.getBoolean("Manager",false))
            {
                ManagerupdateUI()
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
            //Log.d(TAG, "Logging in user.")
            mAuth!!.signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) { task ->
                    mProgressBar!!.hide()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with signed-in user's information
                        //Log.d(TAG, "signInWithEmail:success")


                        mDatabaseReference!!.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                var userflag = snapshot.child(mAuth!!.currentUser!!.uid).child("flag").value.toString()
                                if(userflag == "1") {
                                    if (mail == "omriavidan0402hn@gmail.com") {
                                        Sp!!.edit().putBoolean("logged", true).apply()
                                        UserSp!!.edit().putBoolean("Manager", true).apply()
                                        ManagerupdateUI()
                                    } else {
                                        Sp!!.edit().putBoolean("logged", true).apply()
                                        updateUI()
                                    }
                                    mProgressBar!!.dismiss()
                                    mDatabaseReference!!.removeEventListener(this)
                                    finish()
                                }
                                else
                                    Toast.makeText(this@LoginActivity, "המייל ו/או הסיסמא שגויים",
                                        Toast.LENGTH_SHORT).show()

                            }
                            override fun onCancelled(databaseError: DatabaseError) {}
                        })

                    } else {
                        // If sign in fails, display a message to the user.
                        //Log.e(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(this@LoginActivity, "המייל ו/או הסיסמא שגויים",
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

    private fun ManagerupdateUI() {

        val intent = Intent(this@LoginActivity, ManagerActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}