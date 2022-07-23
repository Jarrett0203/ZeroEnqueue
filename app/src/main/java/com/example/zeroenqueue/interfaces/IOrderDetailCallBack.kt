package com.example.zeroenqueue.interfaces

import com.example.zeroenqueue.classes.Comment
import com.example.zeroenqueue.db.CartItem

interface IOrderDetailCallBack {
    fun onCustomerOrderDetailLoadSuccess(cartItemList:List<CartItem>)
    fun onCustomerOrderDetailLoadFailed(message:String)
}
