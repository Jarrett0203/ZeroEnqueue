package com.example.zeroenqueue.uiCustomer.foodStall

import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.Navigation.setViewNavController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressBack
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.example.zeroenqueue.classes.User
import com.example.zeroenqueue.common.Common
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.FoodStallAdapter
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class FoodStallFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Before
    fun setup() {
        hiltRule.inject()
        Common.currentUser = User()
        Common.currentUser!!.uid = "test"
        Common.currentUser!!.name = "test"
    }

    @Test
    fun testFoodStallFragmentLaunch() {
        launchFragmentInContainer<FoodStallFragment>()
        Thread.sleep(1500)
        onView(withId(R.id.food_stall_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun testClickOnFoodStall() {
        val navController = mock(NavController::class.java)
        val fragmentScenario = launchFragmentInContainer<FoodStallFragment>(themeResId = R.style.Theme_ZeroEnqueue)
        fragmentScenario.onFragment {
            setViewNavController(it.requireView(), navController)
        }
        Thread.sleep(1500)
        onView(withId(R.id.recycler_food_stalls)).perform(RecyclerViewActions.
        actionOnItemAtPosition<FoodStallAdapter.FoodStallViewHolder>(0, click()))
        Thread.sleep(1500)
        verify(navController).navigate(R.id.food_list_fragment)
    }

    fun clickItemWithId(id: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View>? {
                return null
            }

            override fun getDescription(): String {
                return "Click on a child view with specified id."
            }

            override fun perform(uiController: UiController, view: View) {
                val v = view.findViewById<View>(id)
                v.performClick()
            }
        }
    }
}