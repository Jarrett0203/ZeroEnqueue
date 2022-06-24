package com.example.zeroenqueue.activity


import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.database.*
import java.util.*

object RegisterUser {
    @RequiresApi(Build.VERSION_CODES.N)
    fun validateRegistrationInput(
        newEmail: String,
        newPassword: String,
        newName: String,
        newPhone: String,
        firstNums: Array<Char>,
    ) : Boolean {
        if (newName.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty() || newPhone.isEmpty()) {
            return false
        } else if (!Arrays.stream(firstNums).anyMatch { t -> t == newPhone[0] } || newPhone.length != 8)
            return false
        else if (newPassword.length < 6)
            return false
        return true
    }
}