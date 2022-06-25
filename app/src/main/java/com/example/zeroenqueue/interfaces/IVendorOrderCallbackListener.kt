package com.example.zeroenqueue.interfaces

import com.example.zeroenqueue.classes.Order

interface IVendorOrderCallbackListener {
    fun onOrderLoadSuccess(order: List<Order>)
    fun onOrderLoadFailed(message:String)

}