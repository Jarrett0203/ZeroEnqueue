package com.example.zeroenqueue.interfaces

import com.example.zeroenqueue.classes.FoodStall

interface IFoodStallLoadCallback {
    fun onFoodStallLoadSuccess(foodStallList:List<FoodStall>)
    fun onFoodStallLoadFailed(message: String)
}