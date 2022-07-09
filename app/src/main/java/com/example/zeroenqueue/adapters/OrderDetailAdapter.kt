package com.example.zeroenqueue.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.example.zeroenqueue.R
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.db.CartItem
import org.json.JSONArray

class OrderDetailAdapter(private var context: Context, private var cartItemList: List<CartItem>) :
    RecyclerView.Adapter<OrderDetailAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var food_image = itemView.findViewById(R.id.food_image) as ImageView
        var txt_food_name = itemView.findViewById(R.id.txt_food_name) as TextView
        var txt_food_price = itemView.findViewById(R.id.txt_food_price) as TextView
        var txt_size = itemView.findViewById(R.id.txt_size) as TextView
        var txt_addOn = itemView.findViewById(R.id.txt_addOn) as TextView
        var food_quantity = itemView.findViewById(R.id.food_quantity) as TextView

        override fun onClick(view: View?) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.layout_order_detail_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(cartItemList[position].foodImage).into(holder.food_image)
        holder.txt_food_name.text = cartItemList[position].foodName!!
        val foodSizeJsonString =
            StringBuilder("[").append(cartItemList[position].foodSize).append("]").toString()
        val foodAddOnJsonString = cartItemList[position].foodAddon

        if (foodSizeJsonString == "[Default]")
            holder.txt_size.visibility = View.INVISIBLE
        else {
            val jsonArray = JSONArray(foodSizeJsonString)
            val jsonObject = jsonArray.getJSONObject(0)
            val foodSize = jsonObject.get("name").toString()
            holder.txt_size.text = foodSize
        }

        if (foodAddOnJsonString == "Default")
            holder.txt_addOn.visibility = View.INVISIBLE
        else {
            val jsonArray = JSONArray(foodAddOnJsonString)
            var addOnString = ""
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val addOn = jsonObject.get("name").toString()
                addOnString = if (addOnString.isEmpty())
                    addOn
                else
                    StringBuilder(addOnString).append(", ").append(addOn).toString()
            }
            holder.txt_addOn.text = addOnString
        }
        holder.txt_food_price.text = StringBuilder("$ ")
            .append(
                Common.formatPrice(
                    (cartItemList[position].foodPrice + cartItemList[position].foodExtraPrice)
                            * (1 - cartItemList[position].discount)
                )
            )
        holder.food_quantity.text = StringBuilder("x").append(cartItemList[position].foodQuantity)
    }

    override fun getItemCount(): Int = cartItemList.size
}
