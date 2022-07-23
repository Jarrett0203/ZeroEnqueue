package com.example.zeroenqueue.activity

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.example.zeroenqueue.R
import com.example.zeroenqueue.ToastMatcher
import com.example.zeroenqueue.adapters.FoodStallAdapter
import com.example.zeroenqueue.classes.User
import com.example.zeroenqueue.common.Common
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import com.example.zeroenqueue.CommonActions.atPosition
import com.example.zeroenqueue.adapters.CategoryAdapter
import com.example.zeroenqueue.adapters.FoodListAdapter
import com.example.zeroenqueue.adapters.VendorFoodStallAdapter
import com.example.zeroenqueue.classes.Food
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not


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
        Common.currentUser!!.uid = "z2SYlNY4yJbd2NVnZcIKrMHbNH63"
        Common.currentUser!!.name = "test"
        Common.currentUser!!.userType = "Customer"
    }

    @Test
    fun test_isActivityInView() {
        val activityScenario = ActivityScenario.launch(MainCustomerActivity::class.java)
        onView(withId(R.id.customer_home_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun clickCartFabHomeActivity_navigateToCartFragment() {
        val activityScenario = ActivityScenario.launch(MainCustomerActivity::class.java)
        onView(withId(R.id.fabCart)).perform(click())
        onView(withId(R.id.cartFragment)).check(matches(isDisplayed()))
    }

    @Test
    fun clickProfile_navigateToProfileFragment() {
        val activityScenario = ActivityScenario.launch(MainCustomerActivity::class.java)
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.profile_image)).perform(click())
        onView(withId(R.id.profile_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun clickFoodList_navigateToFoodListFragment() {
        val activityScenario = ActivityScenario.launch(MainCustomerActivity::class.java)
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.navigation_food_list)).perform(click())
        onView(withId(R.id.food_list_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun clickFoodStall_navigateToFoodStallFragment() {
        val activityScenario = ActivityScenario.launch(MainCustomerActivity::class.java)
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.navigation_foodStall)).perform(click())
        onView(withId(R.id.food_stall_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun clickCategories_navigateToCategoriesFragment() {
        val activityScenario = ActivityScenario.launch(MainCustomerActivity::class.java)
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.navigation_categories)).perform(click())
        onView(withId(R.id.categories_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun clickOrderSummary_navigateToCustomerOrderSummaryFragment() {
        val activityScenario = ActivityScenario.launch(MainCustomerActivity::class.java)
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.navigation_customer_order_summary)).perform(click())
        onView(withId(R.id.categories_customer_order_summary)).check(matches(isDisplayed()))
    }

    @Test
    fun clickDiscounts_navigateToDiscountsFragment() {
        val activityScenario = ActivityScenario.launch(MainCustomerActivity::class.java)
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.navigation_customerDiscounts)).perform(click())
        onView(withId(R.id.customer_discounts_fragment)).check(matches(isDisplayed()))
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
    fun clickFoodStallItem_navigateToFoodListFragment() {
        val activityScenario = ActivityScenario.launch(MainCustomerActivity::class.java)
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.navigation_foodStall)).perform(click())
        onView(withId(R.id.food_stall_fragment)).check(matches(isDisplayed()))
        onView(withId(R.id.recycler_food_stalls)).perform(RecyclerViewActions.
        actionOnItemAtPosition<FoodStallAdapter.FoodStallViewHolder>(0, click()))
        onView(withId(R.id.food_list_fragment)).check(matches(isDisplayed()))
        onView(withId(R.id.recycler_food_list))
            .check(matches(atPosition(0, hasDescendant(withText("Fish and Chips")))))
        onView(withId(R.id.recycler_food_list))
            .check(matches(atPosition(1, hasDescendant(withText("Carbonara")))))
    }

    @Test
    fun clickCategory_navigateToFoodListFragment() {
        val activityScenario = ActivityScenario.launch(MainCustomerActivity::class.java)
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.navigation_categories)).perform(click())
        onView(withId(R.id.categories_fragment)).check(matches(isDisplayed()))
        onView(withId(R.id.recycler_categories)).perform(RecyclerViewActions.
        actionOnItemAtPosition<CategoryAdapter.CategoryViewHolder>(0, click()))
        onView(withId(R.id.food_list_fragment)).check(matches(isDisplayed()))
        onView(withId(R.id.recycler_food_list))
            .check(matches(atPosition(0, hasDescendant(withText("ROASTED QUARTER CHICKEN")))))
        onView(withId(R.id.recycler_food_list))
            .perform(scrollToPosition<FoodListAdapter.FoodListViewHolder>(1))
            .check(matches(atPosition(1, hasDescendant(withText("CURRY CHICKEN")))))
        onView(withId(R.id.recycler_food_list))
            .perform(scrollToPosition<FoodListAdapter.FoodListViewHolder>(2))
            .check(matches(atPosition(2, hasDescendant(withText("RENDANG CHICKEN")))))
        onView(withId(R.id.recycler_food_list))
            .perform(scrollToPosition<FoodListAdapter.FoodListViewHolder>(3))
            .check(matches(atPosition(3, hasDescendant(withText("HERBAL STEAMED CHICKEN")))))
    }

}