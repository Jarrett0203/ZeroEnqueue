package com.example.zeroenqueue.db

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class LocalCartDataSource(private val cartDAO: CartDAO): CartDataSource {
    override fun getAllCart(uid: String): Flowable<List<CartItem>> {
        return cartDAO.getAllCart(uid)
    }

    override fun getItemInCart(foodId: String, uid: String): Single<CartItem> {
        return cartDAO.getItemInCart(foodId, uid)
    }

    override fun countCartItems(uid: String): Single<Int> {
        return cartDAO.countCartItems(uid)
    }

    override fun totalPrice(uid: String): Single<Double> {
        return cartDAO.totalPrice(uid)
    }

    override fun insertOrReplaceAll(vararg cartItems: CartItem): Completable {
        return cartDAO.insertOrReplaceAll(*cartItems)
    }

    override fun updateCart(cartItem: CartItem): Single<Int> {
        return cartDAO.updateCart(cartItem)
    }

    override fun deleteCart(cartItem: CartItem): Single<Int> {
        return cartDAO.deleteCart(cartItem)
    }

    override fun cleanCart(uid: String): Single<Int> {
        return cartDAO.cleanCart(uid)
    }

    override fun collateFoodStallOrders(uid: String, foodStall: String): Flowable<List<CartItem>> {
        return cartDAO.collateFoodStallOrders(uid, foodStall)
    }

    override fun foodStallTotalPrice(uid: String, foodStall: String): Single<Double> {
        return cartDAO.foodStallTotalPrice(uid, foodStall)
    }

    override fun getItemWithAllOptionsInCart(
        uid: String,
        foodId: String,
        foodSize: String,
        foodAddon: String
    ): Single<CartItem> {
        return cartDAO.getItemWithAllOptionsInCart(uid, foodId, foodSize, foodAddon)
    }
}