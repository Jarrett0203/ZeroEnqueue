package com.example.zeroenqueue.interfaces

import com.example.zeroenqueue.model.Recommended

interface IRecommendedLoadCallback {
    fun onRecommendedLoadSuccess(recommendedList:List<Recommended>)
    fun onRecommendedLoadFailed(message:String)
}