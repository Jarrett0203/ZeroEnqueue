package com.example.zeroenqueue.interfaces

import com.example.zeroenqueue.classes.Recommended

interface IRecommendedLoadCallback {
    fun onRecommendedLoadSuccess(recommendedList:List<Recommended>)
    fun onRecommendedLoadFailed(message:String)
}