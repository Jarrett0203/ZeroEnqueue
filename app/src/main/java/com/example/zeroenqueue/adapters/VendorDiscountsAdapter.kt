package com.example.zeroenqueue.adapters

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zeroenqueue.R
import com.example.zeroenqueue.classes.Discount
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.eventBus.VendorDiscountItemClick
import com.example.zeroenqueue.interfaces.IRecyclerItemClickListener
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat

class VendorDiscountsAdapter(
    var context: Context,
    val discountList: MutableList<Discount>
) : RecyclerView.Adapter<VendorDiscountsAdapter.DiscountsViewHolder>() {
    inner class DiscountsViewHolder(val view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        var foodName: TextView? = itemView.findViewById(R.id.food_name)
        var foodStallName: TextView? = itemView.findViewById(R.id.foodStallName)
        var oldPrice: TextView? = itemView.findViewById(R.id.old_food_price)
        var newPrice: TextView? = itemView.findViewById(R.id.new_food_price)
        var discount: TextView? = itemView.findViewById(R.id.discount)
        var expiry: TextView? = itemView.findViewById(R.id.expiry)
        var foodImage: ImageView? = itemView.findViewById(R.id.food_image)
        var fav_image: ImageView? = itemView.findViewById(R.id.fav_image)
        var cart_image: ImageView? = itemView.findViewById(R.id.cart_image)
        private var listener: IRecyclerItemClickListener? = null

        fun setListener(listener: IRecyclerItemClickListener) {
            this.listener = listener
        }

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            listener!!.onItemClick(view!!, adapterPosition)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VendorDiscountsAdapter.DiscountsViewHolder {
        return DiscountsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_discounts, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: VendorDiscountsAdapter.DiscountsViewHolder,
        position: Int
    ) {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        Glide.with(context).load(discountList[position].foodImage).into(holder.foodImage!!)
        holder.foodName!!.text = discountList[position].foodName
        holder.foodStallName!!.text = discountList[position].foodStallName
        holder.oldPrice!!.text =
            StringBuilder("$ ").append(Common.formatPrice(discountList[position].oldPrice))
        holder.oldPrice!!.paintFlags = holder.oldPrice!!.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        holder.newPrice!!.text =
            StringBuilder("$ ").append(Common.formatPrice(discountList[position].newPrice))
        holder.discount!!.text =
            StringBuilder(discountList[position].discount.toString()).append("%")
        holder.expiry!!.text = StringBuilder("Valid until ").append(
            simpleDateFormat.format(discountList[position].expiry).dropLast(9)
        )
        holder.setListener(object : IRecyclerItemClickListener {
            override fun onItemClick(view: View, pos: Int) {
                Common.discountSelected = discountList[pos]
                EventBus.getDefault().post(VendorDiscountItemClick(true, discountList[pos]))
            }
        })
    }

    override fun getItemCount(): Int = discountList.size

    fun getItemAtPosition(pos: Int): Discount {
        return discountList[pos]
    }

    fun removeItem(pos: Int) {
        discountList.removeAt(pos)
    }


}
