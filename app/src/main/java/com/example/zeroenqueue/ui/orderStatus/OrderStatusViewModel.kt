package com.example.zeroenqueue.ui.orderStatus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OrderStatusViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is order status Fragment"
    }
    val text: LiveData<String> = _text
}