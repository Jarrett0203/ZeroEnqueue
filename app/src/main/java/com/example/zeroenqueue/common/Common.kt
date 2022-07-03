package com.example.zeroenqueue.common

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import com.example.zeroenqueue.classes.*
import com.example.zeroenqueue.db.CartItem
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.abs
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

    fun formatRating(rating: Double): String {
        if (rating != 0.0) {
            val df = DecimalFormat("###0.00")
            df.roundingMode = RoundingMode.HALF_UP
            return StringBuilder(df.format(rating)).toString()
        }
        return rating.toString()
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

    fun setSpanStringColor(s: String, name: String?, txtUser: TextView?, color: Int) {
        val builder = SpannableStringBuilder()
        builder.append(s)
        val txtSpannable = SpannableString(name)
        val boldSpan = StyleSpan(Typeface.BOLD)
        txtSpannable.setSpan(boldSpan, 0, name!!.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        txtSpannable.setSpan(ForegroundColorSpan(color), 0, name.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.append(txtSpannable)
        txtUser!!.setText(builder, TextView.BufferType.SPANNABLE)
    }

    fun getDateOfWeek(i: Int): String {
        return when(i) {
            1 -> "Monday"
            2 -> "Tuesday"
            3 -> "Wednesday"
            4 -> "Thursday"
            5 -> "Friday"
            6 -> "Saturday"
            7 -> "Sunday"
            else -> "unknown"
        }
    }

    fun convertStatusToText(orderStatus: Int): String {
        return when(orderStatus) {
            0 -> "Order placed"
            1 -> "Preparing"
            2 -> "Ready to be collected"
            -1 -> "Cancelled"
            else -> "Unknown"
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
            .append(abs(Random.nextInt()))
            .toString()
    }

    var discountSelected: Discount? = null
    var foodStallSelected: FoodStall? = null
    var categorySelected: Category? = null
    var foodSelected: Food? = null
    var sizeSelected: Size? = null
    var addOnSelected: AddOn? = null
    var cartItemSelected: CartItem? = null
    var currentUser: User? = null

    const val CATEGORY_REF = "Category"
    const val FOODSTALL_REF = "FoodStalls"
    const val ORDER_REF = "Order"
    const val POPULAR_REF = "MostPopular"
    const val RECOMMENDED_REF = "Recommended"
    const val USER_REF = "Users"
    const val FOODLIST_REF = "FoodList"
    const val COMMENT_REF = "Comments"
    const val DISCOUNT_REF = "Discounts"

    const val DATABASE_LINK: String =
        "https://zeroenqueue-default-rtdb.asia-southeast1.firebasedatabase.app/"
    const val FULL_WIDTH_COLUMN: Int = 1
    const val DEFAULT_COLUMN_COUNT: Int = 0

}