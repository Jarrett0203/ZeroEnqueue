package com.example.zeroenqueue.common

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.widget.TextView
import com.example.zeroenqueue.classes.*
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.random.Random
import kotlin.text.StringBuilder

object Common {
    fun formatPrice(price: Double): String {
        if (price != 0.0) {
            val df = DecimalFormat("###0.00")
            df.roundingMode = RoundingMode.HALF_UP
            return StringBuilder(df.format(price)).toString()
        }
        return "0.00"
    }

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
        when (i) {
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
        when (orderStatus) {
            0 -> return "Order placed"
            1 -> return "Preparing"
            2 -> return "Ready to be collected"
            -1 -> return "Cancelled"
            else -> return "Unknown"
        }
    }

    fun calculateExtraPrice(sizeSelected: Size?, addOnSelected: MutableList<AddOn>?): Double {
        var result = 0.0
        if (sizeSelected == null && addOnSelected == null)
            return 0.0
        else if (sizeSelected == null){
            for(addOn in addOnSelected!!){
                result += addOn.price
            }
            return result
        }
        else if (addOnSelected == null){
            result = sizeSelected.price
            return result
        }
        else {
            result = sizeSelected.price
            for(addOn in addOnSelected)
                result += addOn.price
            return result
        }
    }

    fun orderId(): String {
        return StringBuilder()
            .append(System.currentTimeMillis())
            .append(Math.abs(Random.nextInt()))
            .toString()
    }

    var foodStallSelected: FoodStall? = null
    var categorySelected: Category? = null
    var foodSelected: Food? = null
    var currentUser: User? = null

    val CATEGORY_REF: String = "Category"
    val FOODSTALL_REF: String = "FoodStalls"
    val ORDER_REF: String = "Order"
    val POPULAR_REF: String = "MostPopular"
    val RECOMMENDED_REF: String = "Recommended"
    val USER_REF = "Users"
    val FOODLIST_REF = "FoodList"
    val COMMENT_REF = "Comments"

    val DATABASE_LINK: String =
        "https://zeroenqueue-default-rtdb.asia-southeast1.firebasedatabase.app/"
    val FULL_WIDTH_COLUMN: Int = 1
    val DEFAULT_COLUMN_COUNT: Int = 0

}