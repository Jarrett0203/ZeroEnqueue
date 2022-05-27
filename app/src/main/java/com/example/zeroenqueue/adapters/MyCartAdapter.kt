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
import com.example.zeroenqueue.db.CartDataSource
import com.example.zeroenqueue.db.CartDatabase
import com.example.zeroenqueue.db.CartItem
import com.example.zeroenqueue.db.LocalCartDataSource
import com.example.zeroenqueue.eventBus.UpdateCartItems
import com.example.zeroenqueue.ui.foodDetail.FoodDetailViewModel
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.EventBus

class MyCartAdapter (internal var context:Context,
                     internal var cartItem :List<CartItem>) :
    RecyclerView.Adapter<MyCartAdapter.MyViewHolder>() {

    internal var compositeDisposable: CompositeDisposable
    internal var cartDataSource: CartDataSource

    init {
        compositeDisposable = CompositeDisposable()
        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(context).cartDAO())
    }

    inner class MyViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        var img_cart: ImageView
        var txt_food_name: TextView
        var txt_food_prices: TextView
        var number_button: ElegantNumberButton

        init {
            img_cart = itemView.findViewById(R.id.img_cart) as ImageView
            txt_food_name = itemView.findViewById(R.id.txt_food_name) as TextView
            txt_food_prices = itemView.findViewById(R.id.txt_food_price) as TextView
            number_button = itemView.findViewById(R.id.number_button) as ElegantNumberButton
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_cart_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(cartItem[position].foodImage)
            .into(holder.img_cart)
        holder.txt_food_name.text = StringBuilder(cartItem[position].foodName!!)
        holder.txt_food_prices.text = StringBuilder("").append(cartItem[position].foodPrice + cartItem[position].foodExtraPrice)
        holder.number_button.number = cartItem[position].foodQuantity.toString()

        holder.number_button.setOnValueChangeListener { view, oldValue, newValue ->
            cartItem[position].foodQuantity = newValue
            EventBus.getDefault().postSticky(UpdateCartItems(cartItem[position]))
        }

    }

    override fun getItemCount(): Int {
        return cartItem.size
    }


}
