package com.example.zeroenqueue.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.zeroenqueue.databinding.ActivityRegisterUserBinding
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast

class RegisterUser : AppCompatActivity() {

    private lateinit var binding:ActivityRegisterUserBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.registerUser.setOnClickListener {
            val fullName = binding.fullName.text.toString()
            val userType = binding.usertype.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            if (fullName.isEmpty() && userType.isEmpty() && email.isEmpty() && password.isEmpty()) {
                Toast.makeText(this, "Empty fields are not allowed!!", Toast.LENGTH_SHORT).show()
            } else {
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener() {
                    if (it.isSuccessful) {
                        val intent = Intent(this, SignInActivity::class.java)
                        startActivity(intent)
                    } else {
                         Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        val actionBar = supportActionBar
        actionBar!!.title = "RegisterUser"

        actionBar.setDisplayHomeAsUpEnabled(true)
    }
}