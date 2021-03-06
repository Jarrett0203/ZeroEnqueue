package com.example.zeroenqueue.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.zeroenqueue.R
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.db.CartItem
import com.example.zeroenqueue.eventBus.UpdateCartItems
import com.example.zeroenqueue.interfaces.IRecyclerItemClickListener
import org.greenrobot.eventbus.EventBus
import org.json.JSONArray

class MyCartAdapter(
    internal var context: Context,
    private var cartItemList: List<CartItem>
) :
    RecyclerView.Adapter<MyCartAdapter.MyViewHolder>() {

    private var lastCheckedPosition = -1
    private var lastCheckedCheckbox: CheckBox? = null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var img_cart = itemView.findViewById(R.id.img_cart) as ImageView
        var txt_food_name = itemView.findViewById(R.id.txt_food_name) as TextView
        var txt_food_price = itemView.findViewById(R.id.txt_food_price) as TextView
        var txt_size = itemView.findViewById(R.id.txt_size) as TextView
        var txt_addOn = itemView.findViewById(R.id.txt_addOn) as TextView
        var checkbox = itemView.findViewById(R.id.checkbox) as CheckBox
        var number_button = itemView.findViewById(R.id.number_button) as ElegantNumberButton

        override fun onClick(view: View?) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.layout_cart_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(cartItemList[position].foodImage).into(holder.img_cart)
        holder.txt_food_name.text = cartItemList[position].foodName!!

        val foodSizeJsonString =
            StringBuilder("[").append(cartItemList[position].foodSize).append("]").toString()
        val foodAddOnJsonString = cartItemList[position].foodAddon

        if (foodSizeJsonString == "[Default]")
            holder.txt_size.visibility = View.INVISIBLE
        else {
            val jsonArray = JSONArray(foodSizeJsonString)
            val jsonObject = jsonArray.getJSONObject(0)
            val foodSize = jsonObject.get("name").toString()
            holder.txt_size.text = foodSize
        }

        if (foodAddOnJsonString == "Default")
            holder.txt_addOn.visibility = View.INVISIBLE
        else {
            val jsonArray = JSONArray(foodAddOnJsonString)
            var addOnString = ""
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val addOn = jsonObject.get("name").toString()
                addOnString = if (addOnString.isEmpty())
                    addOn
                else
                    StringBuilder(addOnString).append(", ").append(addOn).toString()
            }
            holder.txt_addOn.text = addOnString
        }
        holder.txt_food_price.text = StringBuilder("$ ")
            .append(
                Common.formatPrice(
                    (cartItemList[position].foodPrice + cartItemList[position].foodExtraPrice)
                            * (1 - cartItemList[position].discount) * cartItemList[position].foodQuantity
                )
            )
        holder.number_button.number = cartItemList[position].foodQuantity.toString()
        holder.number_button.setOnValueChangeListener { _, _, newValue ->
            cartItemList[position].foodQuantity = newValue
            EventBus.getDefault().postSticky(UpdateCartItems(cartItemList[position]))
        }
        holder.checkbox.setOnClickListener {
            if (holder.checkbox.isChecked) {
                if (lastCheckedCheckbox != null)
                    lastCheckedCheckbox!!.isChecked = false
                Common.cartItemSelected = cartItemList[holder.adapterPosition]
                lastCheckedCheckbox = holder.checkbox
                lastCheckedPosition = holder.adapterPosition
            } else {
                Common.cartItemSelected = null
                lastCheckedCheckbox = null
                lastCheckedPosition = -1
            }
        }

    }

    override fun getItemCount(): Int = cartItemList.size

    fun getItemAtPosition(pos: Int): CartItem = cartItemList[pos]
}
