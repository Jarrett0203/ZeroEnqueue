package com.example.zeroenqueue.db

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface CartDAO {
    @Query("SELECT * FROM Cart WHERE uid=:uid")
    fun getAllCart(uid:String): Flowable<List<CartItem>>

    @Query("SELECT * FROM Cart WHERE foodId=:foodId AND uid=:uid")
    fun getItemInCart(foodId:String, uid:String): Single<CartItem>

    @Query("SELECT SUM(foodQuantity) FROM Cart WHERE uid=:uid")
    fun countCartItems(uid:String): Single<Int>

    @Query("SELECT SUM((foodPrice + foodExtraPrice) * foodQuantity * (1 - discount)) FROM Cart WHERE uid=:uid")
    fun totalPrice(uid:String): Single<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceAll(vararg cartItems: CartItem):Completable

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateCart(cartItem: CartItem):Single<Int>

    @Delete
    fun deleteCart(cartItem: CartItem):Single<Int>

    @Query("DELETE FROM Cart WHERE uid=:uid")
    fun cleanCart(uid:String): Single<Int>

    @Query("SELECT * FROM Cart WHERE foodId=:foodId AND uid=:uid AND foodSize =:foodSize AND foodAddon =:foodAddon")
    fun getItemWithAllOptionsInCart(uid:String, foodId:String, foodSize:String, foodAddon:String): Single<CartItem>
}