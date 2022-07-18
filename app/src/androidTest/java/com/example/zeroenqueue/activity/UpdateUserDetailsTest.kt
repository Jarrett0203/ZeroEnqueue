package com.example.zeroenqueue.activity

import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.zeroenqueue.R
import com.example.zeroenqueue.classes.User
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.launchFragmentInHiltContainer
import com.example.zeroenqueue.uiCustomer.profile.ProfileFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.android.synthetic.main.activity_register_user.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.layout_upload_nus_card.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class updateUserDetailsTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    @Throws(Exception::class)
    fun setUp() {
        hiltRule.inject()
        Common.currentUser = User()
        Common.currentUser!!.uid = "test"
        Common.currentUser!!.name = "test"
        Common.currentUser!!.userType = "customer"
    }

    @Test
    fun correctUserDetailsInfo() {
        val result = updateUserDetails.validateUpdateDetailsInput(
            "user@gmail.com",
            "1234567",
            "user",
            "99999999"
        )
        Assert.assertEquals(result, true)
    }

    @Test
    fun successUpdate()  {
        launchFragmentInHiltContainer<ProfileFragment> {
            val name = this.input_name
            name.setText("test2")
            val updatedName = this.input_name
            btnUpdate.performClick()
            assertEquals("test2", updatedName.text.toString())
        }
    }

    @Test
    fun updatePicSuccess()  {
        launchFragmentInHiltContainer<ProfileFragment> {

            Common.currentUser!!.image = "https://images.pexels.com/photos/771742/pexels-photo-771742.jpeg?auto=compress&cs=tinysrgb&w=1600"


            val nusCardPic = this.addNUSCardImage
            assertNotNull(nusCardPic)
        }
    }

    @Test
    fun changeToVendorType()  {
        val fragmentScenario = FragmentScenario.launch(ProfileFragment::class.java)
        //onView(withId(R.id.chipVendor)).perform(set)
        onView(withId(R.id.btnUpdate)).perform(click())
        //onView(withId(R.id.)).perform(click())
        onView(withId(R.id.cartFragment)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//        launchFragmentInHiltContainer<ProfileFragment> {
//            chipVendor.isChecked = true
//            chipCustomer.isChecked = false
//            btnUpdate.performClick()
//            dialog.findViewById<View>(android.R.id.button1).performClick()
//
//            assertEquals(false, chipCustomer.isChecked)
//        }
    }

    @Test
    fun changeToCustomerType()  {
        launchFragmentInHiltContainer<ProfileFragment> {
            chipVendor.isChecked = true
            chipCustomer.isChecked = false
            btnUpdate.performClick()
            dialog.findViewById<View>(android.R.id.button2).performClick()

            assertEquals(true, chipVendor.isChecked)
        }
    }

}