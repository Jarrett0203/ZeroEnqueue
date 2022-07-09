package com.example.zeroenqueue.adapters

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zeroenqueue.R
import com.example.zeroenqueue.classes.Discount
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.interfaces.IRecyclerItemClickListener
import java.text.SimpleDateFormat

class UseDiscountsAdapter(var context: Context,
                          val discountList: List<Discount>
) : RecyclerView.Adapter<UseDiscountsAdapter.UseDiscountsViewHolder>() {

    private var lastCheckedPosition = -1
    private var lastCheckedCheckbox: CheckBox? = null

    inner class UseDiscountsViewHolder(val view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        var checkbox: CheckBox = itemView.findViewById(R.id.checkbox)
        var discountDescription: TextView = itemView.findViewById(R.id.txt_description)
        var foodImage: ImageView = itemView.findViewById(R.id.food_image)
        var foodName: TextView = itemView.findViewById(R.id.txt_food_name)
        var expiry: TextView = itemView.findViewById(R.id.expiry)
        var oldPrice: TextView = itemView.findViewById(R.id.txt_old_price)
        var newPrice: TextView = itemView.findViewById(R.id.txt_new_price)
        override fun onClick(v: View?) {
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UseDiscountsAdapter.UseDiscountsViewHolder {
        return UseDiscountsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_use_discount_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: UseDiscountsAdapter.UseDiscountsViewHolder, position: Int) {
        val simpleDateFormat = SimpleDateFormat("dd/mm/yyyy")
        Glide.with(context).load(discountList[position].foodImage)
            .into(holder.foodImage)
        holder.discountDescription.text = discountList[position].description
        holder.foodName.text = discountList[position].foodName
        holder.oldPrice.text = StringBuilder("$ ").append(Common.formatPrice(discountList[position].oldPrice))
        holder.oldPrice.paintFlags = holder.oldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        holder.newPrice.text = StringBuilder("$ ").append(Common.formatPrice(discountList[position].newPrice))
        holder.expiry.text = StringBuilder("Valid until ").append(simpleDateFormat.format(discountList[position].expiry))
        holder.checkbox.setOnClickListener {
            if (holder.checkbox.isChecked) {
                if (lastCheckedCheckbox != null)
                    lastCheckedCheckbox!!.isChecked = false
                Common.discountSelected = discountList[holder.adapterPosition]
                lastCheckedCheckbox = holder.checkbox
                lastCheckedPosition = holder.adapterPosition
            }
            else {
                Common.discountSelected = null
                lastCheckedCheckbox = null
                lastCheckedPosition = -1
            }
        }
    }

    override fun getItemCount(): Int = discountList.size
}
