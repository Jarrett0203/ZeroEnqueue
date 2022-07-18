package com.example.zeroenqueue.activity



import android.view.Gravity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.example.zeroenqueue.R
import com.example.zeroenqueue.classes.User
import com.example.zeroenqueue.common.Common
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock


@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class MainCustomerActivityTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
        Common.currentUser = User()
        Common.currentUser!!.uid = "test"
        Common.currentUser!!.name = "test"
    }

    @Test
    fun clickCartFabHomeActivity_navigateToCartFragment() {
        val activityScenario = ActivityScenario.launch(MainCustomerActivity::class.java)
        onView(withId(R.id.fabCart)).perform(click())
        onView(withId(R.id.cartFragment)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToFoodStallPage() {
        val navController = mock(NavController::class.java)
        val activityScenario = ActivityScenario.launch(MainCustomerActivity::class.java)

        onView(withId(R.id.nav_customer)).perform(DrawerActions.open());
        onView(withId(R.id.navigation_foodStall)).perform(click())
        onView(withId(R.id.cartFragment)).check(matches(isDisplayed()))
//        onView(withId(R.id.nav_customer))
//            .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
//            .perform(DrawerActions.open());
//        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
//
//        onView(withText("Food Stalls"))
//            .perform(click())

        onView(withId(R.id.cartFragment)).check(matches(isDisplayed()))
    }


}