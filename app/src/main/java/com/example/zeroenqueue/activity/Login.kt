package com.example.zeroenqueue.activity

object Login {

    fun validate(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty()
    }
}