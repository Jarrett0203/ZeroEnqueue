package com.example.zeroenqueue.activity

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import com.example.zeroenqueue.CommonActions.clickItemWithId
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.AddNewDiscountAdapter
import com.example.zeroenqueue.adapters.VendorDiscountsAdapter
import com.example.zeroenqueue.adapters.VendorFoodStallAdapter
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
    fun clickDiscounts_navigateToVendorDiscountsSummaryFragment() {
        val activityScenario = ActivityScenario.launch(MainVendorActivity::class.java)
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.navigation_vendor_discounts)).perform(click())
        onView(withId(R.id.vendor_discounts_fragment)).check(matches(isDisplayed()))
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
    fun changeToCustomerType()  {
        val activityScenario = ActivityScenario.launch(MainVendorActivity::class.java)
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.navigation_sign_out)).perform(click())
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.loginActivity)).check(matches(isDisplayed()))
        onView(withId(R.id.email)).perform(typeText("john@gmail.com"))
        onView(withId(R.id.password)).perform(typeText("password"))
        onView(withId(R.id.login)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.recycler_food_stalls)).perform(RecyclerViewActions
            .actionOnItemAtPosition<VendorFoodStallAdapter.FoodStallViewHolder>(0, click()))
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.profile_image)).perform(click())
        onView(withId(R.id.chipCustomer)).perform(click())
        onView(withId(R.id.btnUpdate)).perform(click())
        onView(withText("OK")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.customer_home_fragment)).check(matches(isDisplayed()))
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.profile_image)).perform(click())
        onView(withId(R.id.chipVendor)).perform(click())
        onView(withId(R.id.btnUpdate)).perform(click())
        onView(withText("OK")).inRoot(isDialog()).perform(click())
        onView(withId(R.id.stalls_overview_activity)).check(matches(isDisplayed()))
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
        val device: UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val appViews = UiScrollable(UiSelector().scrollable(true))
        appViews.scrollForward()
        onView(withId(R.id.addNewFood)).perform(click())
        onView(withId(R.id.vendor_food_detail_fragment)).check(matches(isDisplayed()))
    }

/*    @Test
    fun vendorOrderNavigation() {
        val activityScenario = ActivityScenario.launch(MainVendorActivity::class.java)
        Thread.sleep(1000)
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.navigation_vendor_order_summary)).perform(click())
        onView(withId(R.id.recycler_order)).perform(RecyclerViewActions
            .actionOnItemAtPosition<VendorMyOrderAdapter.MyViewHolder>(0, swipeLeft()))

        onView(
            hasDescendant(withText("Edit"))).perform(click())
        onView(withId(R.id.vendor_food_detail_fragment)).check(matches(isDisplayed()))
    }*/

    @Test
    fun discountItem_navigateToVendorDiscountDetailsFragment() {
        val activityScenario = ActivityScenario.launch(MainVendorActivity::class.java)
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.navigation_vendor_discounts)).perform(click())
        onView(withId(R.id.vendor_discounts_fragment)).check(matches(isDisplayed()))
        onView(withId(R.id.recyclerDiscounts)).perform(RecyclerViewActions
            .actionOnItemAtPosition<VendorDiscountsAdapter.DiscountsViewHolder>(0, click()))
        onView(withId(R.id.vendor_discount_details_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun discountFragment_navigateToAddNewDiscountFragment() {
        val activityScenario = ActivityScenario.launch(MainVendorActivity::class.java)
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.navigation_vendor_discounts)).perform(click())
        onView(withId(R.id.vendor_discounts_fragment)).check(matches(isDisplayed()))
        onView(withId(R.id.addNewDiscounts)).perform(click())
        onView(withId(R.id.add_new_discount_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun addNewDiscountFragment_navigateToVendorDiscountDetailsFragment() {
        val activityScenario = ActivityScenario.launch(MainVendorActivity::class.java)
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.navigation_vendor_discounts)).perform(click())
        onView(withId(R.id.vendor_discounts_fragment)).check(matches(isDisplayed()))
        onView(withId(R.id.addNewDiscounts)).perform(click())
        onView(withId(R.id.add_new_discount_fragment)).check(matches(isDisplayed()))
        onView(withId(R.id.recycler_food_list)).perform(RecyclerViewActions
            .actionOnItemAtPosition<AddNewDiscountAdapter.MyViewHolder>(0, clickItemWithId(R.id.checkbox)))
        onView(withId(R.id.btn_add_new_discount)).perform(click())
        onView(withId(R.id.vendor_discount_details_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun vendorDiscountDetailsFragment_navigateToDiscountsFragment() {
        val activityScenario = ActivityScenario.launch(MainVendorActivity::class.java)
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.navigation_vendor_discounts)).perform(click())
        onView(withId(R.id.vendor_discounts_fragment)).check(matches(isDisplayed()))
        onView(withId(R.id.recyclerDiscounts)).perform(RecyclerViewActions
            .actionOnItemAtPosition<VendorDiscountsAdapter.DiscountsViewHolder>(0, click()))
        onView(withId(R.id.vendor_discount_details_fragment)).check(matches(isDisplayed()))
        val device: UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val appViews = UiScrollable(UiSelector().scrollable(true))
        appViews.scrollForward()
        onView(withId(R.id.btnConfirmChanges)).perform(click())
        onView(withId(R.id.vendor_discounts_fragment)).check(matches(isDisplayed()))
    }



}