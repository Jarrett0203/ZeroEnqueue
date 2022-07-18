package com.example.zeroenqueue.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.zeroenqueue.R
import com.example.zeroenqueue.classes.User
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import dmax.dialog.SpotsDialog

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dialog: AlertDialog
    private lateinit var userRef: DatabaseReference
    private lateinit var listener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val registerBtn = findViewById<Button>(R.id.registerBtn)
        registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterUserActivity::class.java)
            startActivity(intent)
        }

        firebaseAuth = FirebaseAuth.getInstance()
        dialog = SpotsDialog.Builder().setContext(this).setCancelable(false).build()
        userRef = FirebaseDatabase.getInstance().getReference(Common.USER_REF)

        listener = FirebaseAuth.AuthStateListener {
            val user = firebaseAuth.currentUser
            if (user != null)
                checkUserFromFirebase(user)
        }

        binding.login.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            if (Login.validate(email, password)) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    dialog.show()
                    if (it.isSuccessful) {
                        if (firebaseAuth.currentUser != null) {
                            userRef.child(firebaseAuth.currentUser!!.uid)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()) {
                                            val currentUser = snapshot.getValue(User::class.java)
                                            goToMainActivity(currentUser)
                                        }
                                        dialog.dismiss()
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(
                                            this@LoginActivity,
                                            error.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                        }
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }

                }
            } else {
                Toast.makeText(this, "Empty fields are not allowed!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkUserFromFirebase(user: FirebaseUser) {
        dialog.show()
        userRef.child(user.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@LoginActivity, error.message, Toast.LENGTH_SHORT).show()
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(User::class.java)
                        goToMainActivity(user)
                    }
                }
            })
    }

    private fun goToMainActivity(user: User?) {

        FirebaseMessaging.getInstance().token.addOnFailureListener {
            Toast.makeText(this@LoginActivity, it.message, Toast.LENGTH_SHORT).show()
            Common.currentUser = user!!
            if (user.userType == "Customer") {
                val intent = Intent(this, MainCustomerActivity::class.java)
                startActivity(intent)
            }
            else {
                val intent = Intent(this, StallsOverviewActivity::class.java)
                startActivity(intent)
            }
        }
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Common.currentUser = user!!
                    Common.updateToken(this, it.result!!)
                    if (user.userType == "Customer") {
                        val intent = Intent(this, MainCustomerActivity::class.java)
                        startActivity(intent)
                    }
                    else {
                        val intent = Intent(this, StallsOverviewActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            dialog.show()
            userRef.child(firebaseAuth.currentUser!!.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val currentUser = snapshot.getValue(User::class.java)
                            goToMainActivity(currentUser)
                        }
                        dialog.dismiss()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@LoginActivity,
                            error.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    override fun onStop() {
        firebaseAuth.removeAuthStateListener(listener)
        super.onStop()
    }
}



