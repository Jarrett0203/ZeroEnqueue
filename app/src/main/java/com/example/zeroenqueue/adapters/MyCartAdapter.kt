package com.example.zeroenqueue.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.zeroenqueue.R
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.db.CartDataSource
import com.example.zeroenqueue.db.CartDatabase
import com.example.zeroenqueue.db.CartItem
import com.example.zeroenqueue.db.LocalCartDataSource
import com.example.zeroenqueue.eventBus.UpdateCartItems
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.EventBus
import org.json.JSONArray

class MyCartAdapter(
    internal var context: Context,
    internal var cartItem: List<CartItem>
) :
    RecyclerView.Adapter<MyCartAdapter.MyViewHolder>() {

    internal var compositeDisposable: CompositeDisposable = CompositeDisposable()
    internal var cartDataSource: CartDataSource =
        LocalCartDataSource(CartDatabase.getInstance(context).cartDAO())

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img_cart = itemView.findViewById(R.id.img_cart) as ImageView
        var txt_food_name = itemView.findViewById(R.id.txt_food_name) as TextView
        var txt_food_price = itemView.findViewById(R.id.txt_food_price) as TextView
        var txt_size = itemView.findViewById(R.id.txt_size) as TextView
        var txt_addOn = itemView.findViewById(R.id.txt_addOn) as TextView
        var number_button = itemView.findViewById(R.id.number_button) as ElegantNumberButton

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.layout_cart_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(cartItem[position].foodImage).into(holder.img_cart)
        holder.txt_food_name.text = cartItem[position].foodName!!

        val foodSizeJsonString = StringBuilder("[").append(cartItem[position].foodSize).append("]").toString()
        val foodAddOnJsonString = cartItem[position].foodAddon

        if (foodSizeJsonString == "[Default]")
            holder.txt_size.visibility = View.GONE
        else {
            val jsonArray = JSONArray(foodSizeJsonString)
            val jsonObject = jsonArray.getJSONObject(0)
            val foodSize = jsonObject.get("name").toString()
            holder.txt_size.text = foodSize
        }

        if (foodAddOnJsonString == "Default")
            holder.txt_addOn.visibility = View.GONE
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
            .append(Common.formatPrice(cartItem[position].foodPrice + cartItem[position].foodExtraPrice))
            .toString()
        holder.number_button.number = cartItem[position].foodQuantity.toString()

        holder.number_button.setOnValueChangeListener { _, _, newValue ->
            cartItem[position].foodQuantity = newValue
            EventBus.getDefault().postSticky(UpdateCartItems(cartItem[position]))
        }

    }

    override fun getItemCount(): Int {
        return cartItem.size
    }

    fun getItemAtPosition(pos: Int): CartItem {
        return cartItem[pos]
    }


}
