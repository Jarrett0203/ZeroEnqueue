package com.example.zeroenqueue.ui.foodStall

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FoodStallViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is food stall Fragment"
    }
    val text: LiveData<String> = _text
}