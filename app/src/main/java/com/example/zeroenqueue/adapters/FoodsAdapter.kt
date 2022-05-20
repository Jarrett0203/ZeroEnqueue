package com.example.zeroenqueue.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zeroenqueue.model.Food
import com.example.zeroenqueue.R
import kotlinx.android.synthetic.main.layout_food_item.view.*

class FoodsAdapter(val foods: List<Food>) : RecyclerView.Adapter<FoodsAdapter.FoodsViewHolder>() {
    class FoodsViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodsViewHolder {
        return FoodsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_food_item, parent, false)
        )
    }

    override fun getItemCount(): Int = foods.size

    override fun onBindViewHolder(holder: FoodsViewHolder, position: Int) {
        val food = foods[position]

        holder.view.tvPrice.text = "$"+food.price.toString()
        /*holder.view.tvRating.text = food.rating.toString() + "%"
        holder.view.tvReviewCount.text = food.reviewCount.toString() + " reviews"
        holder.view.tvFoodName.text = food.foodName
        holder.view.tvFoodStallName.text = food.foodStallName
        holder.view.tvCategories.text = food.categories
        holder.view.tvIsNew.visibility = if(food.isNew) View.VISIBLE else View.*/
    }
}