package com.example.zeroenqueue.activity

import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import com.example.zeroenqueue.R
import com.google.firebase.database.core.view.View
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class LoginActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    @Throws(Exception::class)
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun successfulLogin()  {
        val activityScenario = ActivityScenario.launch(LoginActivity::class.java)
        activityScenario.onActivity { activity ->
             run {
                 val email = activity!!.findViewById<EditText>(R.id.email)
                 val pass = activity!!.findViewById<EditText>(R.id.password)
                 email.setText("test@gmail.com")
                 pass.setText("password1")
                 val loginBtn = activity!!.findViewById<Button>(R.id.login)
                 loginBtn.performClick()
                 assertNotNull(activity!!.findViewById<android.view.View>(R.id.homepage))
             }
        }

    }

    @Test
    fun failedLogin()  {
        val activityScenario = ActivityScenario.launch(LoginActivity::class.java)
        activityScenario.onActivity { activity ->
            run {
                val email = activity!!.findViewById<EditText>(R.id.email)
                val pass = activity!!.findViewById<EditText>(R.id.password)
                email.setText("test@gmail.com")
                pass.setText("wrongPassword1")
                val loginBtn = activity!!.findViewById<Button>(R.id.login)
                loginBtn.performClick()
                assertNull(activity!!.findViewById<android.view.View>(R.id.homepage))
            }
        }

    }

}
