package com.example.zeroenqueue.activity

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.example.zeroenqueue.R
import com.example.zeroenqueue.classes.FoodStall
import com.example.zeroenqueue.classes.User
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.launchFragmentInHiltContainer
import com.example.zeroenqueue.uiCustomer.customerHome.CustomerHomeFragment
import com.example.zeroenqueue.uiCustomer.foodList.FoodListFragment
import com.example.zeroenqueue.uiCustomer.profile.ProfileFragment
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.layout_filter_food.*
import org.junit.Assert
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class FoodListFragmentTest {
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
    fun filterChicken() {
        launchFragmentInHiltContainer<FoodListFragment> {
            filter_fab.performClick()
            var foodStallArrList = arrayListOf<FoodStall>()
            var categoryArrList = arrayListOf("CHICKEN")
            this.filter(foodStallArrList, categoryArrList)
            onView(withId(R.id.btnFilter)).perform(click())
        }
    }

}