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
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_register_user.*
import java.util.*


class RegisterUser : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterUserBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userRef: DatabaseReference
    private lateinit var dialog: AlertDialog
    private lateinit var chipCustomer: Chip
    private lateinit var chipVendor: Chip

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRef = FirebaseDatabase.getInstance().getReference(Common.USER_REF)
        firebaseAuth = Firebase.auth
        dialog = SpotsDialog.Builder().setContext(this).setCancelable(false).build()


        binding.registerUser.setOnClickListener {
            chipCustomer = binding.chipCustomer
            chipVendor = binding.chipVendor
            val newEmail = binding.email.text.toString().trim()
            val newPassword = binding.password.text.toString().trim()
            val newName = binding.fullName.text.toString().trim()
            val newPhone = binding.phone.text.toString().trim()
            val firstNums = arrayOf('6', '8', '9')

            if (newName.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty() || newPhone.isEmpty())
                Toast.makeText(this, "Empty fields are not allowed!!", Toast.LENGTH_SHORT).show()
            else if (!chipCustomer.isChecked && !chipVendor.isChecked)
                Toast.makeText(this, "Please select a user type.", Toast.LENGTH_SHORT).show()
            else if (!Arrays.stream(firstNums).anyMatch { t -> t == newPhone[0] } || newPhone.length != 8)
                Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show()
            else if (newPassword.length < 6)
                Toast.makeText(this, "Password requires at least 6 characters", Toast.LENGTH_SHORT).show()
            else {
                firebaseAuth.createUserWithEmailAndPassword(newEmail, newPassword)
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
                                            currentUser.name = newName
                                            currentUser.phone = newPhone
                                            currentUser.email = newEmail
                                            currentUser.password = newPassword
                                            if (chipCustomer.isChecked)
                                                currentUser.userType = "Customer"
                                            else
                                                currentUser.userType = "Vendor"

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

