package com.example.zeroenqueue

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class RegisterUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)

        val actionBar = supportActionBar

        actionBar!!.title = "RegisterUser"

        actionBar.setDisplayHomeAsUpEnabled(true)
    }
}