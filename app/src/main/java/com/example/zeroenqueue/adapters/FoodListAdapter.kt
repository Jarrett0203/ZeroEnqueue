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
import com.example.zeroenqueue.db.CartDataSource
import com.example.zeroenqueue.db.CartDatabase
import com.example.zeroenqueue.db.CartItem
import com.example.zeroenqueue.db.LocalCartDataSource
import com.example.zeroenqueue.eventBus.CountCartEvent
import com.example.zeroenqueue.eventBus.FoodItemClick
import com.example.zeroenqueue.interfaces.IRecyclerItemClickListener
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus

class FoodListAdapter(
    var context: Context,
    val foodList: List<Food>
) : RecyclerView.Adapter<FoodListAdapter.FoodListViewHolder>() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val cartDataSource: CartDataSource = LocalCartDataSource(CartDatabase.getInstance(context).cartDAO())

    inner class FoodListViewHolder(val view: View) : RecyclerView.ViewHolder(view),
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
    ): FoodListAdapter.FoodListViewHolder {
        return FoodListViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_food_item, parent, false)
        )
    }

    override fun getItemCount(): Int = foodList.size

    override fun onBindViewHolder(
        holder: FoodListAdapter.FoodListViewHolder,
        position: Int
    ) {
        Glide.with(context).load(foodList[position].image)
            .into(holder.food_image!!)
        holder.food_name!!.text = foodList[position].name
        holder.food_price!!.text = Common.formatPrice(foodList[position].price)
        Common.foodStallSelected = null
        Common.categorySelected = null

        holder.setListener(object : IRecyclerItemClickListener {
            override fun onItemClick(view: View, pos: Int) {
                Common.foodSelected = foodList[pos]
                EventBus.getDefault().postSticky(FoodItemClick(true, foodList[pos]))
            }
        })

        holder.cart_image!!.setOnClickListener {
            val cartItem = CartItem()
            cartItem.uid = Common.currentUser!!.uid!!
            cartItem.userPhone = Common.currentUser!!.phone!!
            cartItem.foodStall = foodList[position].foodStall!!
            cartItem.foodId = foodList[position].id!!
            cartItem.foodName = foodList[position].name!!
            cartItem.foodImage = foodList[position].image!!
            cartItem.foodPrice = foodList[position].price
            cartItem.foodQuantity = 1
            cartItem.foodExtraPrice = 0.0
            cartItem.foodAddon = "Default"
            cartItem.foodSize = "Default"

            cartDataSource.getItemWithAllOptionsInCart(Common.currentUser!!.uid!!,
                cartItem.foodId,
                cartItem.foodSize,
                cartItem.foodAddon
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: SingleObserver<CartItem> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(cartItemFromDB: CartItem) {
                        //if item is alr in db, update
                        if(cartItemFromDB == cartItem) {
                            cartItemFromDB.foodExtraPrice = cartItem.foodExtraPrice
                            cartItemFromDB.foodAddon = cartItem.foodAddon
                            cartItemFromDB.foodSize = cartItem.foodSize
                            cartItemFromDB.foodQuantity = cartItemFromDB.foodQuantity + cartItem.foodQuantity

                            cartDataSource.updateCart(cartItemFromDB)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object:SingleObserver<Int> {
                                    override fun onSuccess(t: Int) {
                                        Toast.makeText(context, "Update Cart Success", Toast.LENGTH_SHORT).show()
                                        EventBus.getDefault().postSticky(CountCartEvent(true))
                                    }

                                    override fun onSubscribe(d: Disposable) {

                                    }

                                    override fun onError(e: Throwable) {
                                        Toast.makeText(context, "[UPDATE CART]" + e.message, Toast.LENGTH_SHORT).show()
                                    }
                                })
                        } else {
                            //insert if it is not avail.
                            compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT)
                                        .show()
                                    EventBus.getDefault().postSticky(CountCartEvent(true))
                                }, {
                                        t: Throwable -> Toast.makeText(context, "{INSERT CART}" + t.message, Toast.LENGTH_SHORT).show()
                                }))
                        }
                    }

                    override fun onError(e: Throwable) {
                        if(e.message!!.contains("empty")) {
                            compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT)
                                        .show()
                                    EventBus.getDefault().postSticky(CountCartEvent(true))
                                }, {
                                    t: Throwable -> Toast.makeText(context, "{INSERT CART}" + t.message, Toast.LENGTH_SHORT).show()
                                }))
                        } else {
                            Toast.makeText(context, "[CART ERROR]" + e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }
    }

    fun onStop() {
        compositeDisposable.clear()
    }
}