package com.example.zeroenqueue.common

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.widget.TextView
import com.example.zeroenqueue.classes.*

object Common {
    fun setSpanString(s: String, name: String?, txtUser: TextView?) {
        val builder = SpannableStringBuilder()
        builder.append(s)
        val txtSpannable = SpannableString(name)
        val boldSpan = StyleSpan(Typeface.BOLD)
        txtSpannable.setSpan(boldSpan, 0, name!!.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.append(txtSpannable)
        txtUser!!.setText(builder, TextView.BufferType.SPANNABLE)
    }

    fun getDateOfWeek(i: Int): String {
        when(i) {
            1 -> return "Monday"
            2 -> return "Tuesday"
            3 -> return "Wednesday"
            4 -> return "Thursday"
            5 -> return "Friday"
            6 -> return "Saturday"
            7 -> return "Sunday"
            else -> return "unknown"
        }
    }

    fun convertStatusToText(orderStatus: Int): String {
        when(orderStatus) {
            0 -> return "Order placed"
            1 -> return "Preparing"
            2 -> return "Ready to be collected"
            -1 -> return "Cancelled"
            else -> return "Unknown"
        }
    }

    var foodStallSelected: FoodStall?=null
    var categorySelected: Category?=null
    var foodSelected: Food?=null
    var currentUser: User?= null
    var foodList: List<Food>?= null

    val CATEGORY_REF: String ="Category"
    val CATEGORIES_REF: String ="Categories"
    val FOODSTALL_REF: String = "FoodStalls"
    val ORDER_REF: String = "Order"
    val POPULAR_REF: String ="MostPopular"
    val RECOMMENDED_REF: String ="Recommended"
    val USER_REF="Users"
    val FOODLIST_REF = "FoodList"
    val COMMENT_REF = "Comments"

    val DATABASE_LINK: String ="https://zeroenqueue-default-rtdb.asia-southeast1.firebasedatabase.app/"
    val FULL_WIDTH_COLUMN: Int = 1
    val DEFAULT_COLUMN_COUNT: Int = 0

}