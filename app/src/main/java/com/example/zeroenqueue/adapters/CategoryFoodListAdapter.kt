package com.example.zeroenqueue.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zeroenqueue.R
import com.example.zeroenqueue.classes.Comment
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.eventBus.FoodItemClick
import com.example.zeroenqueue.interfaces.IRecyclerItemClickListener
import com.example.zeroenqueue.classes.Food
import org.greenrobot.eventbus.EventBus

class CategoryFoodListAdapter(
    var context: Context,
    val foodList: List<Food>
) : RecyclerView.Adapter<CategoryFoodListAdapter.CategoryFoodViewHolder>() {

    inner class CategoryFoodViewHolder(val view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        var food_name: TextView? = null
        var food_price: TextView? = null
        var food_image: ImageView? = null
        var fav_image: ImageView? = null
        var cart_image: ImageView? = null
        private var listener: IRecyclerItemClickListener? = null

        fun setListener(listener: IRecyclerItemClickListener) {
            this.listener = listener
        }

        init {
            food_name = itemView.findViewById(R.id.food_name)
            food_price = itemView.findViewById(R.id.food_price)
            food_image = itemView.findViewById(R.id.food_image)
            fav_image = itemView.findViewById(R.id.fav_image)
            cart_image = itemView.findViewById(R.id.cart_image)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            listener!!.onItemClick(view!!, adapterPosition)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryFoodListAdapter.CategoryFoodViewHolder {
        return CategoryFoodViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_food_item, parent, false)
        )
    }

    override fun getItemCount(): Int = foodList.size

    override fun onBindViewHolder(
        holder: CategoryFoodListAdapter.CategoryFoodViewHolder,
        position: Int
    ) {
        Glide.with(context).load(foodList[position].image)
            .into(holder.food_image!!)
        holder.food_name!!.text = foodList[position].name
        holder.food_price!!.text = foodList[position].price.toString()

        holder.setListener(object : IRecyclerItemClickListener {
            override fun onItemClick(view: View, pos: Int) {
                Common.foodSelected = foodList[pos]
                Common.foodSelected!!.key = pos.toString()
                EventBus.getDefault().postSticky(FoodItemClick(true, foodList[pos]))
            }
        } )
    }
}