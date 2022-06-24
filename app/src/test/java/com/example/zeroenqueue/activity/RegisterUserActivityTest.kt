package com.example.zeroenqueue.activity

import android.R
import android.widget.Button
import kotlinx.android.synthetic.main.activity_register_user.*
import org.junit.Assert.*
import org.junit.Test


class RegisterUserActivityTest {

    @Test
    fun `correct user inputs returns true`() {
        val result = RegisterUser.validateRegistrationInput(
            "user@gmail.com",
            "1234567",
            "user",
            "99999999",
            arrayOf('6', '8', '9')
        )
        assertEquals(result, true)
    }

    @Test
    fun checkLoginButton() {

    }
}