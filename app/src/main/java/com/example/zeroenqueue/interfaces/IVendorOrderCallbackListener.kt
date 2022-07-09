package com.example.zeroenqueue.interfaces

import com.example.zeroenqueue.classes.Order

interface IVendorOrderCallbackListener {
    fun onOrderLoadSuccess(order: MutableList<Order>)
    fun onOrderLoadFailed(message:String)

}