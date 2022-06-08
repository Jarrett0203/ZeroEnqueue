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
import com.example.zeroenqueue.classes.FoodStall
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.eventBus.FoodStallClick
import com.example.zeroenqueue.eventBus.VendorFoodStallClick
import com.example.zeroenqueue.interfaces.IRecyclerItemClickListener
import org.greenrobot.eventbus.EventBus

class VendorFoodStallAdapter(
    var context: Context,
    val foodStallList: List<FoodStall>
) : RecyclerView.Adapter<VendorFoodStallAdapter.FoodStallViewHolder>() {

    inner class FoodStallViewHolder(val view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        var food_stall_name: TextView? = null
        var food_stall_address: TextView? = null
        var food_stall_image: ImageView? = null
        private var listener: IRecyclerItemClickListener?=null

        fun setListener(listener: IRecyclerItemClickListener){
            this.listener = listener
        }

        init {
            food_stall_name = itemView.findViewById(R.id.food_stall_name)
            food_stall_address = itemView.findViewById(R.id.food_stall_address)
            food_stall_image = itemView.findViewById(R.id.food_stall_image)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            listener!!.onItemClick(view!!,adapterPosition)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VendorFoodStallAdapter.FoodStallViewHolder {
        return FoodStallViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_food_stall, parent, false)
        )
    }

    override fun getItemCount(): Int = foodStallList.size

    override fun onBindViewHolder(
        holder: VendorFoodStallAdapter.FoodStallViewHolder,
        position: Int
    ) {
        Glide.with(context).load(foodStallList[position].image)
            .into(holder.food_stall_image!!)
        holder.food_stall_name!!.text = foodStallList[position].name
        holder.food_stall_address!!.text = foodStallList[position].address

        holder.setListener(object : IRecyclerItemClickListener {
            override fun onItemClick(view: View, pos: Int) {
                Common.foodStallSelected = foodStallList[pos]
                EventBus.getDefault().postSticky(VendorFoodStallClick(true, foodStallList[pos]))
            }
        })
    }
    /*override fun getItemViewType(position: Int): Int {
        return if(foodStallList.size == 1)
            Common.DEFAULT_COLUMN_COUNT
        else{
            if(foodStallList.size % 2 == 0)
                Common.DEFAULT_COLUMN_COUNT
            else
                if(position>1 && position == foodStallList.size-1)
                    Common.FULL_WIDTH_COLUMN else Common.DEFAULT_COLUMN_COUNT

        }
    }*/
}