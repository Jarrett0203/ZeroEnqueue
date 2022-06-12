package com.example.zeroenqueue.uiCustomer.orderStatus

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zeroenqueue.classes.Order

class OrderSummaryModel : ViewModel() {
    val mutableLiveDataOrderList:MutableLiveData<List<Order>>
    init {
        mutableLiveDataOrderList = MutableLiveData()
    }

//    fun setMutableLiveDataOrderList.value(orderList: List<Order>) {
//        mutableLiveDataOrderList.value = orderList
//    }
}
