package com.example.zeroenqueue.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.zeroenqueue.classes.User
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.ActivityRegisterUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import dmax.dialog.SpotsDialog


class RegisterUser : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterUserBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userRef: DatabaseReference
    private lateinit var dialog: AlertDialog
    private lateinit var fullName: TextView
    private lateinit var userType: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRef = FirebaseDatabase.getInstance().getReference(Common.USER_REF)
        firebaseAuth = Firebase.auth
        dialog = SpotsDialog.Builder().setContext(this).setCancelable(false).build()


        binding.registerUser.setOnClickListener {
            fullName = binding.fullName
            userType = binding.usertype
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            if (fullName.text.toString().isEmpty() && userType.text.toString()
                    .isEmpty() && email.isEmpty() && password.isEmpty()
            ) {
                Toast.makeText(this, "Empty fields are not allowed!!", Toast.LENGTH_SHORT).show()
            } else {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            val newUser = firebaseAuth.currentUser
                            dialog.show()
                            userRef.child(newUser!!.uid)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()) {
                                            val currentUser = snapshot.getValue(User::class.java)
                                            goToMainActivity(currentUser)
                                        } else {
                                            val currentUser = User()
                                            currentUser.uid = newUser.uid
                                            currentUser.name = fullName.text.toString()
                                            currentUser.userType = userType.text.toString()
                                            userRef.child(newUser.uid)
                                                .setValue(currentUser)
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        Toast.makeText(
                                                            this@RegisterUser,
                                                            "Registration success",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                    goToMainActivity(currentUser)
                                                }
                                        }
                                        dialog.dismiss()
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(
                                            this@RegisterUser,
                                            error.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        /*
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.title = "RegisterUser"

        actionBar.setDisplayHomeAsUpEnabled(true)*/
    }

    private fun goToMainActivity(user: User?) {
        Common.currentUser = user!!
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}

