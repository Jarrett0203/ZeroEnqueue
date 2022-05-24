package com.example.zeroenqueue.db

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface CartDataSource {
    fun getAllCart(uid:String): Flowable<List<CartItem>>

    fun getItemInCart(foodId:String, uid:String): Single<CartItem>

    fun countCartItems(uid:String): Single<Int>

    fun totalPrice(uid:String): Single<Long>

    fun insertOrReplaceAll(vararg cartItems: CartItem): Completable

    fun updateCart(cartItem: CartItem): Single<Int>

    fun deleteCart(cartItem: CartItem): Single<Int>

    fun cleanCart(uid:String): Single<Int>
}