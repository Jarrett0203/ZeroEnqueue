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
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.eventBus.CategoryClick
import com.example.zeroenqueue.interfaces.IRecyclerItemClickListener
import com.example.zeroenqueue.classes.Category
import org.greenrobot.eventbus.EventBus

class CategoryAdapter(
    var context: Context,
    val categories: List<Category>
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(val view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        var category_name: TextView? = null
        var category_image: ImageView? = null
        private var listener:IRecyclerItemClickListener?=null

        fun setListener(listener: IRecyclerItemClickListener){
            this.listener = listener
        }

        init {
            category_name = itemView.findViewById(R.id.category_name)
            category_image = itemView.findViewById(R.id.category_image)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            listener!!.onItemClick(view!!,adapterPosition)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryAdapter.CategoryViewHolder {
        return CategoryViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_category_item, parent, false)
        )
    }

    override fun getItemCount(): Int = categories.size

    override fun onBindViewHolder(
        holder: CategoryAdapter.CategoryViewHolder,
        position: Int
    ) {
        Glide.with(context).load(categories[position].image)
            .into(holder.category_image!!)
        holder.category_name!!.text = categories[position].name

        holder.setListener(object : IRecyclerItemClickListener {
            override fun onItemClick(view: View, pos: Int) {
                Common.categorySelected = categories[pos]
                EventBus.getDefault().postSticky(CategoryClick(true, categories[pos]))
            }
        })
    }
    override fun getItemViewType(position: Int): Int {
        return if(categories.size == 1)
            Common.DEFAULT_COLUMN_COUNT
        else{
            if(categories.size % 2 == 0)
                Common.DEFAULT_COLUMN_COUNT
            else
                if(position>1 && position == categories.size-1)
                    Common.FULL_WIDTH_COLUMN else Common.DEFAULT_COLUMN_COUNT

        }
    }
}