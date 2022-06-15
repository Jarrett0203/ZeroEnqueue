package com.example.zeroenqueue.db

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Cart", primaryKeys = ["uid", "foodId", "foodSize", "foodAddon"])
class CartItemVendor {
    var foodId:String = ""

    var uid:String = ""

    var userPhone:String = ""

    var foodName:String? = null

    var foodImage:String? = null

    var foodStall:String? = null

    var foodPrice:Double = 0.0

    var foodExtraPrice:Double = 0.0

    var foodQuantity:Int = 0

    var foodAddon:String = ""

    var foodSize:String = ""

}