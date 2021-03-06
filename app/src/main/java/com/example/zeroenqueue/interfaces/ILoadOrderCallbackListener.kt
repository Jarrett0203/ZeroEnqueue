package com.example.zeroenqueue.interfaces

import android.view.View
import com.example.zeroenqueue.classes.Order

interface ILoadOrderCallbackListener {


    fun onLoadOrderSuccess(orderList: List<Order>)
    fun onLoadOrderFailed(message: String)
}