package com.example.zeroenqueue.uiCustomer.orders

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zeroenqueue.classes.Order

class CustomerOrderSummaryViewModel : ViewModel() {
    val mutableLiveDataOrderList:MutableLiveData<List<Order>> = MutableLiveData()

    fun setMutableLiveDataOrderList(orderList: List<Order>) {
        mutableLiveDataOrderList.value = orderList
    }
}
