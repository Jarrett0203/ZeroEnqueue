package com.example.zeroenqueue.common

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.zeroenqueue.R
import com.example.zeroenqueue.classes.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.collection.LLRBNode
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
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

    fun getNewOrderTopic(): String {
        return StringBuilder("/topics/new_order").toString()
    }

    fun updateToken(context: Context, token: String) {
        FirebaseDatabase.getInstance()
            .getReference(Common.TOKEN_REF)
            .child(Common.currentUser!!.uid!!)
            .setValue(TokenModel(Common.currentUser!!.phone!!, token))
            .addOnFailureListener {e -> Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()}




    }


    fun showNotification(context: Context, id:Int, title: String?, content: String?, intent: Intent?) {
        var pendingIntent : PendingIntent ?= null
        if(intent != null)
            pendingIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val NOTIFICATION_CHANNEL_ID = "com.example.zeroenqueue"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID,
                "ZeroEnqueue", NotificationManager.IMPORTANCE_DEFAULT)

            notificationChannel.description = "ZeroEnqueue"
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.lightColor = (Color.RED)
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)

            notificationManager.createNotificationChannel(notificationChannel)
        }

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)

        builder.setContentTitle(title!!).setContentText(content!!).setAutoCancel(true)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_baseline_menu_24))

        if(pendingIntent != null)
            builder.setContentIntent(pendingIntent)

        val notification = builder.build()
        notificationManager.notify(id, notification)

    }


    var discountSelected: Discount? = null
    var foodStallSelected: com.example.zeroenqueue.classes.FoodStall? = null
    var categorySelected: Category? = null
    var foodSelected: Food? = null
    var sizeSelected: Size? = null
    var addOnSelected: AddOn? = null
    var cartItemSelected: CartItem? = null
    var currentUser: User? = null

    const val NOTI_TITLE = "title"
    const val NOTI_CONTENT = "content"
    const val TOKEN_REF = "Tokens"
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