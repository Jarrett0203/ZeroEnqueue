package com.example.zeroenqueue.adapters

import com.example.zeroenqueue.common.Common
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zeroenqueue.R
import com.example.zeroenqueue.classes.FoodStall
import com.example.zeroenqueue.classes.Order
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class MyOrderAdapter(private val context: Context,
                     private val orderList:List<Order>) :
    RecyclerView.Adapter<MyOrderAdapter.MyViewHolder>() {

    internal var calendar: Calendar
    internal var simpleDateFormat: SimpleDateFormat

    init {
        calendar = Calendar.getInstance()
        simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.layout_order_item_customer, parent, false))
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img_order = itemView.findViewById(R.id.img_order) as ImageView
        var txt_order_comment = itemView.findViewById(R.id.txt_order_comment) as TextView
        var txt_order_status = itemView.findViewById(R.id.txt_order_status) as TextView
        var txt_order_number = itemView.findViewById(R.id.txt_order_number) as TextView
        var txt_order_date = itemView.findViewById(R.id.txt_order_date) as TextView
        var txt_food_stall = itemView.findViewById(R.id.txt_food_stall) as TextView
  }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context)
            .load(orderList[position].cartItemList!![0].foodImage)
            .into(holder.img_order!!)
        calendar.timeInMillis = orderList[position].createDate
        val date = Date(orderList[position].createDate)
        holder.txt_order_date!!.text = StringBuilder(Common.getDateOfWeek(calendar.get(Calendar.DAY_OF_WEEK)))
            .append(" ")
            .append(simpleDateFormat.format(date))
        holder.txt_order_number!!.text = StringBuilder("Order number: ").append(orderList[position].orderNumber)
        holder.txt_order_comment!!.text = StringBuilder("Comment: ").append(orderList[position].comment)
        holder.txt_order_status!!.text = StringBuilder("Status: ").append(Common.convertStatusToText(orderList[position].orderStatus))
        FirebaseDatabase.getInstance(Common.DATABASE_LINK).getReference(Common.FOODSTALL_REF).child(orderList[position].foodStallId!!)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val foodStall = snapshot.getValue(FoodStall::class.java)
                        holder.txt_food_stall!!.text = foodStall!!.name
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        holder.txt_food_stall!!.text
    }


    override fun getItemCount(): Int {
        return orderList.size
    }

}