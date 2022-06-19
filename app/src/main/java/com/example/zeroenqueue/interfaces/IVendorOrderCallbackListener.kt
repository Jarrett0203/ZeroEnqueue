package com.example.zeroenqueue.interfaces

import com.example.zeroenqueue.uiVendor.orders.OrderModel

interface IVendorOrderCallbackListener {
    fun onOrderLoadSuccess(orderModel: List<OrderModel>)
    fun onOrderLoadFailed(message:String)

}