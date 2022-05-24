package com.example.zeroenqueue.db

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Cart")
class CartItem {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "foodId")
    var foodId:String = ""

    @ColumnInfo(name = "uid")
    var uid:String = ""

    @ColumnInfo(name = "userPhone")
    var userPhone:String = ""

    @ColumnInfo(name = "foodName")
    var foodName:String? = null

    @ColumnInfo(name = "foodImage")
    var foodImage:String? = null

    @ColumnInfo(name = "foodStall")
    var foodStall:String? = null

    @ColumnInfo(name = "foodPrice")
    var foodPrice:Double = 0.0

    @ColumnInfo(name = "foodExtraPrice")
    var foodExtraPrice:Double = 0.0

    @ColumnInfo(name = "foodQuantity")
    var foodQuantity:Int = 0

    @ColumnInfo(name = "foodAddOn")
    var foodAddOn:String? = null

    @ColumnInfo(name = "foodSize")
    var foodSize:String? = null
}