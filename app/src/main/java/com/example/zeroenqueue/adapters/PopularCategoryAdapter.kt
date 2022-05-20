package com.example.zeroenqueue.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zeroenqueue.R
import com.example.zeroenqueue.model.PopularCategory
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.layout_popular_categories_item.view.*

class PopularCategoryAdapter(var context:Context,
    val popularCategories: List<PopularCategory>)
    : RecyclerView.Adapter<PopularCategoryAdapter.PopularCategoryViewHolder>() {
    inner class PopularCategoryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var category_name:TextView ?= null
        var category_image:CircleImageView ?= null

        init {
            category_name = itemView.findViewById(R.id.txt_recommend)
            category_image = itemView.findViewById(R.id.category_image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularCategoryAdapter.PopularCategoryViewHolder {
        return PopularCategoryViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_popular_categories_item, parent, false)
        )
    }

    override fun getItemCount(): Int = popularCategories.size

    override fun onBindViewHolder(holder: PopularCategoryAdapter.PopularCategoryViewHolder, position: Int) {
        Glide.with(context).load(popularCategories.get(position).image).into(holder.view.category_image)
        holder.view.text_category_name!!.setText(popularCategories.get(position).name)
    }
}