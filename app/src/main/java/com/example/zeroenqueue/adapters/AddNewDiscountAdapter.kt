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
import com.example.zeroenqueue.classes.Food
import com.example.zeroenqueue.common.Common

class AddNewDiscountAdapter(var context: Context, val foodList: List<Food>) : RecyclerView.Adapter<AddNewDiscountAdapter.MyViewHolder>() {
    private var lastCheckedPosition = -1
    private var lastCheckedCheckbox: CheckBox? = null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var food_image = itemView.findViewById(R.id.food_image) as ImageView
        var txt_food_name = itemView.findViewById(R.id.txt_food_name) as TextView
        var txt_food_price = itemView.findViewById(R.id.txt_food_price) as TextView
        var checkbox = itemView.findViewById(R.id.checkbox) as CheckBox

        override fun onClick(view: View?) {

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.layout_food_item_for_new_discount, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(foodList[position].image).into(holder.food_image)
        holder.txt_food_name.text = foodList[position].name!!
        holder.txt_food_price.text = Common.formatPrice(foodList[position].price)
        holder.checkbox.setOnClickListener {
            if (holder.checkbox.isChecked) {
                if (lastCheckedCheckbox != null)
                    lastCheckedCheckbox!!.isChecked = false
                Common.foodSelected = foodList[holder.adapterPosition]
                lastCheckedCheckbox = holder.checkbox
                lastCheckedPosition = holder.adapterPosition
            } else {
                Common.foodSelected = null
                lastCheckedCheckbox = null
                lastCheckedPosition = -1
            }
        }
    }

    override fun getItemCount(): Int = foodList.size
}
