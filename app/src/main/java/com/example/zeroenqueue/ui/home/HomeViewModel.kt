package com.example.zeroenqueue.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.zeroenqueue.FoodItem

class HomeViewModel : ViewModel() {

    private val _foodItems = MutableLiveData<List<FoodItem>>().apply {
        value = listOf(
            FoodItem(1, "https://i1.lensdump.com/i/tJ76mZ.png", 5.50, 78, 132123, "Fish and Chips",
                "The Tea Party", "Western", true),
            FoodItem(2, "https://i1.lensdump.com/i/tJ76mZ.png", 5.50,78, 132123, "Fish and Chips",
                "The Tea Party", "Western", false),
            FoodItem(3, "https://i1.lensdump.com/i/tJ76mZ.png",5.50, 78, 132123, "Fish and Chips",
                "The Tea Party", "Western", false)
        )
    }
    val foodItems: LiveData<List<FoodItem>> =_foodItems
}