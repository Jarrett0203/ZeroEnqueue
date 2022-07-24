package com.example.zeroenqueue.activity

import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.RootMatchers
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
        val activityScenario = ActivityScenario.launch(MainCustomerActivity::class.java)
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.navigation_sign_out)).perform(click())
        onView(withText("OK")).inRoot(RootMatchers.isDialog()).check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.loginActivity)).check(matches(isDisplayed()))
        onView(withId(R.id.email)).perform(typeText("john@gmail.com"))
        onView(withId(R.id.password)).perform(typeText("password"))
        onView(withId(R.id.login)).perform(click())
        onView(withId(R.id.login)).perform(click())
        onView(withId(R.id.login)).perform(click())
        onView(withId(R.id.customer_home_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun failedLogin() {
        val activityScenario = ActivityScenario.launch(MainCustomerActivity::class.java)
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.navigation_sign_out)).perform(click())
        onView(withText("OK")).inRoot(RootMatchers.isDialog()).check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.loginActivity)).check(matches(isDisplayed()))
        onView(withId(R.id.email)).perform(typeText("test@gmail.com"))
        onView(withId(R.id.password)).perform(typeText("wrongPassword1"))
        onView(withId(R.id.login)).perform(click())
        onView(withId(R.id.loginActivity)).check(matches(isDisplayed()))
    }

}
