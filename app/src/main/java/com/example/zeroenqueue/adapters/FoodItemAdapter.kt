package com.example.zeroenqueue.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zeroenqueue.dataClass.FoodItem
import com.example.zeroenqueue.R
import kotlinx.android.synthetic.main.layout_fooditem.view.*

class FoodItemAdapter(val foodItems: List<FoodItem>) : RecyclerView.Adapter<FoodItemAdapter.FoodItemViewHolder>() {
    class FoodItemViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {
        return FoodItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_fooditem, parent, false)
        )
    }

    override fun getItemCount(): Int = foodItems.size

    override fun onBindViewHolder(holder: FoodItemViewHolder, position: Int) {
        val foodItem = foodItems[position]

        holder.view.tvPrice.text = "$"+foodItem.price.toString()
        holder.view.tvRating.text = foodItem.rating.toString() + "%"
        holder.view.tvReviewCount.text = foodItem.reviewCount.toString() + " reviews"
        holder.view.tvFoodName.text = foodItem.foodName
        holder.view.tvFoodStallName.text = foodItem.foodStallName
        holder.view.tvCategories.text = foodItem.categories
        holder.view.tvIsNew.visibility = if(foodItem.isNew) View.VISIBLE else View.INVISIBLE
    }
}