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
import com.example.zeroenqueue.classes.Food
import com.example.zeroenqueue.classes.Order
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.uiVendor.orders.OrderModel
import java.text.SimpleDateFormat

class VendorMyOrderAdapter (
    internal var context: Context,
    internal val orderList: MutableList<OrderModel>) : RecyclerView.Adapter<VendorMyOrderAdapter.MyViewHolder>() {

    lateinit var simpleDateFormat: SimpleDateFormat

    init {
        simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
    }

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var txt_time: TextView? = null
        var txt_order_number: TextView? = null
        var txt_order_status: TextView? = null
        var txt_num_item: TextView? = null
        var txt_name: TextView? = null
        var img_food_image: ImageView? = null

        init {
            img_food_image = itemView.findViewById(R.id.img_food_image) as ImageView
            txt_time = itemView.findViewById(R.id.txt_time) as TextView
            txt_order_status = itemView.findViewById(R.id.txt_order_status) as TextView
            txt_order_number = itemView.findViewById(R.id.txt_order_number) as TextView
            txt_num_item = itemView.findViewById(R.id.txt_num_item) as TextView
            txt_name = itemView.findViewById(R.id.txt_name) as TextView
        }
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
            .into(holder.img_food_image!!)
        holder.txt_order_number!!.setText(orderList[position].key)
        Common.setSpanStringColor("Order date", simpleDateFormat.format(orderList[position].createDate),
            holder.txt_time, Color.parseColor("#333639"))

        Common.setSpanStringColor("Order status", Common.convertStatusToText(orderList[position].orderStatus),
            holder.txt_time, Color.parseColor("#00574B"))

        Common.setSpanStringColor("Num of items", if(orderList[position].cartItemList == null) "0"
        else orderList[position].cartItemList!!.size.toString(),
        holder.txt_num_item, Color.parseColor(("#00574B")))

        Common.setSpanStringColor("Name", orderList[position].userName,
            holder.txt_name, Color.parseColor("#00574B"))
    }

    fun getItemAtPosition(pos: Int): OrderModel {
        return orderList[pos]
    }

    fun removeItem(pos: Int) {
        orderList.removeAt(pos)
    }

}