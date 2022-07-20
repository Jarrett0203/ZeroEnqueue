package com.example.zeroenqueue.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zeroenqueue.R
import com.example.zeroenqueue.classes.Order
import com.example.zeroenqueue.common.Common
import java.text.SimpleDateFormat

class VendorMyOrderAdapter (
    internal var context: Context,
    private val orderList: MutableList<Order>) : RecyclerView.Adapter<VendorMyOrderAdapter.MyViewHolder>() {

    private var simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var txt_time =  itemView.findViewById(R.id.txt_vendor_order_status) as TextView
        var txt_order_number = itemView.findViewById(R.id.txt_order_number) as TextView
        var txt_num_item = itemView.findViewById(R.id.txt_num_item) as TextView
        var txt_name = itemView.findViewById(R.id.txt_name) as TextView
        var img_food_image = itemView.findViewById(R.id.img_food_image) as ImageView
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.layout_order_item_vendor, parent, false))
    }


    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(orderList[position].cartItemList!![0].foodImage)
            .into(holder.img_food_image)
        holder.txt_order_number.text = simpleDateFormat.format(orderList[position].createDate)

        Common.setSpanStringColor("Order status: ", Common.convertStatusToText(orderList[position].orderStatus),
            holder.txt_time, Color.parseColor("#00574B"))

        Common.setSpanStringColor("Customer Name: ", orderList[position].userName,
            holder.txt_name, Color.parseColor("#00574B"))

        var sum = 0
        for (item in orderList[position].cartItemList!!) {
            sum += item.foodQuantity
        }
        Common.setSpanStringColor("Num of items: ", sum.toString(),
        holder.txt_num_item, Color.parseColor("#00574B"))

    }

    fun getItemAtPosition(pos: Int): Order {
        return orderList[pos]
    }

    fun removeItem(pos: Int) {
        orderList.removeAt(pos)
    }

}