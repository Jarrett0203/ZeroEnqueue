package com.example.zeroenqueue.interfaces

import com.example.zeroenqueue.classes.Food

interface IFoodLoadCallback {
    fun onFoodLoadSuccess(categoryList:List<Food>)
    fun onFoodLoadFailed(message:String)
}