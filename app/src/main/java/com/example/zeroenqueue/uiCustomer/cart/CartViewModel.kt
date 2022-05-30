package com.example.zeroenqueue.uiCustomer.cart

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.db.CartDataSource
import com.example.zeroenqueue.db.CartDatabase
import com.example.zeroenqueue.db.CartItem
import com.example.zeroenqueue.db.LocalCartDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CartViewModel: ViewModel() {
    private val compositeDisposable: CompositeDisposable
    private lateinit var cartDataSource: CartDataSource
    private var mutableLiveDataCartItem: MutableLiveData<List<CartItem>>?=null


    init {
        compositeDisposable = CompositeDisposable()
    }

    fun getMutableLiveDataCartItems(): MutableLiveData<List<CartItem>> {
        if(mutableLiveDataCartItem == null)
            mutableLiveDataCartItem = MutableLiveData()
        getCartItems()
        return mutableLiveDataCartItem!!
    }

    fun initCartDataSource(context: Context) {
        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(context).cartDAO())
    }

    private fun getCartItems() {
        compositeDisposable.addAll(
            cartDataSource.getAllCart(Common.currentUser!!.uid!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({cartItems ->
                mutableLiveDataCartItem!!.value = cartItems
            }, {t: Throwable? -> mutableLiveDataCartItem!!.value = null }))
    }

    fun onStop() {
        compositeDisposable.clear()
    }


}