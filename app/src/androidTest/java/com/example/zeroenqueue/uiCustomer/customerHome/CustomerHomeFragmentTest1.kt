package com.example.zeroenqueue.uiCustomer.customerHome


import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.example.zeroenqueue.R
import com.example.zeroenqueue.activity.MainCustomerActivity
import com.example.zeroenqueue.classes.User
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify


@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class CustomerHomeFragmentTest1 {
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
    fun navigateToFoodStallPage() {
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<CustomerHomeFragment> {
            Navigation.setViewNavController(requireView(), navController)
        }

//        val activityScenario = ActivityScenario.launch(MainCustomerActivity::class.java)
//        onView(withId(R.id.navigation_foodStall)).perform(click())
//        onView(withId(R.id.cartFragment)).check(matches(isDisplayed()))
//        onView(withId(R.id.nav_customer))
//            .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
//            .perform(DrawerActions.open());
//        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)

        onView(withText("Food Stalls"))
            .perform(click())

        verify(navController).navigate(
            R.id.navigation_foodStall
        )
    }


}