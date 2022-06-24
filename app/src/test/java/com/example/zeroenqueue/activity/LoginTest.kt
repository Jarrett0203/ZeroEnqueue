package com.example.zeroenqueue.activity

import android.R
import android.widget.Button
import android.widget.EditText
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.apache.maven.wagon.PathUtils.password
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class LoginTest {
        @Test
        fun `login successful test`()  {
            // ...when the string is returned from the object under test...
            val result: String = Login.validate("user", "user")

            // ...then the result should be the expected one.
            assertEquals(result, FAKE_STRING)
        }

        companion object {
            private const val FAKE_STRING = "Login was successful"
        }
    }

//class LoginTest {
//    @Rule
//    var mActivityTestRule: ActivityTestRule<LoginActivity> = ActivityTestRule<LoginActivity>(LoginActivity::class.java)
//    var loginActivity: LoginActivity? = null
//    @Before
//    @Throws(Exception::class)
//    fun setUp() {
//        loginActivity = mActivityTestRule.getActivity()
//    }
//
//    @Test
//    fun testLogin() {
//        getInstrumentation().runOnMainSync(Runnable {
//            val email = loginActivity!!.findViewById<EditText>(R.id.email)
//            val pass = loginActivity!!.findViewById<EditText>(R.id.password)
//            email.setText("email")
//            pass.setText("pass")
//            val loginBtn = loginActivity!!.findViewById<Button>(R.id.login)
//            loginBtn.performClick()
//            assertTrue(loginActivity.isCurUserLoggedIn())
//        })
//    }
//
//    @After
//    @Throws(Exception::class)
//    fun tearDown() {
//        loginActivity = null
//    }
//}