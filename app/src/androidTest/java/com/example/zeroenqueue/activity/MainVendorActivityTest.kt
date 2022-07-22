package com.example.zeroenqueue.activity

import androidx.appcompat.view.menu.MenuAdapter
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.VendorFoodListAdapter
import com.example.zeroenqueue.classes.FoodStall
import com.example.zeroenqueue.classes.User
import com.example.zeroenqueue.common.Common
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class MainVendorActivityTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
        Common.currentUser = User()
        Common.foodStallSelected = FoodStall()
        Common.currentUser!!.uid = "CcJLWPgwhnWdXHSq1jLjG0q9ikv1"
        Common.currentUser!!.name = "John"
        Common.currentUser!!.userType = "Vendor"
        Common.foodStallSelected!!.id = "-N5Kwp-nVosgqXPHVY9i"
        Common.foodStallSelected!!.image = "https://firebasestorage.googleapis.com/v0/b/zeroenqueue.appspot.com/o/stallImage%2FCcJLWPgwhnWdXHSq1jLjG0q9ikv1?alt=media&token=4b520f84-d8a8-4a23-a73f-10483b761f71"
        Common.foodStallSelected!!.address = "2 Sports Drive 1, #03-03, 117561"
        Common.foodStallSelected!!.description = "The Tea Party"
        Common.foodStallSelected!!.name = "The Tea Party"
        Common.foodStallSelected!!.phone = "96991655"
    }

    @Test
    fun test_isActivityInView() {
        val activityScenario = ActivityScenario.launch(MainVendorActivity::class.java)
        onView(withId(R.id.vendor_home_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun clickProfile_navigateToProfileFragment() {
        val activityScenario = ActivityScenario.launch(MainVendorActivity::class.java)
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.profile_image)).perform(click())
        onView(withId(R.id.profile_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun clickStallMenu_navigateToStallMenuFragment() {
        val activityScenario = ActivityScenario.launch(MainVendorActivity::class.java)
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.navigation_stall_menu)).perform(click())
        onView(withId(R.id.menu_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun clickOrders_navigateToVendorOrderSummaryFragment() {
        val activityScenario = ActivityScenario.launch(MainVendorActivity::class.java)
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.navigation_vendor_order_summary)).perform(click())
        onView(withId(R.id.vendor_order_summary_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun clickSwitchStall_navigateToStallOverviewFragment() {
        val activityScenario = ActivityScenario.launch(MainVendorActivity::class.java)
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.navigation_switch_stall)).perform(click())
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.stalls_overview_activity)).check(matches(isDisplayed()))
    }

    @Test
    fun clickSignOut() {
        val activityScenario = ActivityScenario.launch(MainCustomerActivity::class.java)
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.navigation_sign_out)).perform(click())
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.loginActivity)).check(matches(isDisplayed()))
    }

    @Test
    fun foodItem_navigateToEditFoodDetailFragment() {
        val activityScenario = ActivityScenario.launch(MainVendorActivity::class.java)
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.navigation_stall_menu)).perform(click())
        onView(withId(R.id.menu_fragment)).check(matches(isDisplayed()))
        onView(withId(R.id.recycler_menu_list)).perform(RecyclerViewActions
            .actionOnItemAtPosition<VendorFoodListAdapter.FoodListViewHolder>(0, click()))
        onView(withId(R.id.vendor_food_detail_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun addNewFoodItem_navigateToEditFoodDetailFragment() {
        val activityScenario = ActivityScenario.launch(MainVendorActivity::class.java)
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.navigation_stall_menu)).perform(click())
        onView(withId(R.id.menu_fragment)).check(matches(isDisplayed()))
        onView(withId(R.id.addNewFood)).perform(click())
        onView(withId(R.id.vendor_food_detail_fragment)).check(matches(isDisplayed()))
    }

}