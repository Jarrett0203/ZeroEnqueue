package com.example.zeroenqueue.uiCustomer.orderStatus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OrderStatusViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Waiting time:"

    }
    val text: LiveData<String> = _text
}