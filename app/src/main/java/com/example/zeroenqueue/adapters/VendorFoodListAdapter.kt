package com.example.zeroenqueue.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zeroenqueue.R
import com.example.zeroenqueue.classes.Food
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.eventBus.FoodItemClick
import com.example.zeroenqueue.eventBus.VendorFoodItemClick
import com.example.zeroenqueue.interfaces.IRecyclerItemClickListener
import org.greenrobot.eventbus.EventBus

class VendorFoodListAdapter(
    var context: Context,
    val foodList: List<Food>
) : RecyclerView.Adapter<VendorFoodListAdapter.FoodListViewHolder>() {

    inner class FoodListViewHolder(val view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        var food_name: TextView? = null
        var food_price: TextView? = null
        var food_image: ImageView? = null
        var food_rating: TextView? = null
        var food_review_count: TextView? = null
        private var listener: IRecyclerItemClickListener? = null

        fun setListener(listener: IRecyclerItemClickListener) {
            this.listener = listener
        }

        init {
            food_name = itemView.findViewById(R.id.food_name)
            food_price = itemView.findViewById(R.id.food_price)
            food_image = itemView.findViewById(R.id.food_image)
            food_rating = itemView.findViewById(R.id.food_rating)
            food_review_count = itemView.findViewById(R.id.food_review_count)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            listener!!.onItemClick(view!!, adapterPosition)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VendorFoodListAdapter.FoodListViewHolder {
        return FoodListViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_food_item_vendor, parent, false)
        )
    }

    override fun getItemCount(): Int = foodList.size

    override fun onBindViewHolder(
        holder: VendorFoodListAdapter.FoodListViewHolder,
        position: Int
    ) {
        Glide.with(context).load(foodList[position].image)
            .into(holder.food_image!!)
        holder.food_name!!.text = foodList[position].name
        holder.food_price!!.text =
            StringBuilder("").append(Common.formatPrice(foodList[position].price)).toString()
        val rating = (foodList[position].ratingValue)/(foodList[position].ratingCount)
        if (rating.isNaN())
            holder.food_rating!!.text = "0.0"
        else if (holder.food_rating != null)
            holder.food_rating!!.text = Common.formatRating(rating.toDouble())
        if (holder.food_review_count != null)
            if (foodList[position].ratingCount == 1L)
                holder.food_review_count!!.text = StringBuilder(foodList[position].ratingCount.toString()).append(" review")
            else
                holder.food_review_count!!.text = StringBuilder(foodList[position].ratingCount.toString()).append(" reviews")


        holder.setListener(object : IRecyclerItemClickListener {
            override fun onItemClick(view: View, pos: Int) {
                Common.foodSelected = foodList[pos]
                Common.foodSelected!!.key = foodList[pos].id
                EventBus.getDefault().postSticky(VendorFoodItemClick(true, foodList[pos]))
            }
        })
    }
}