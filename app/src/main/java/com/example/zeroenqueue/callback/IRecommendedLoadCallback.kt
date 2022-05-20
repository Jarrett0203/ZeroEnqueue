package com.example.zeroenqueue.callback

import com.example.zeroenqueue.dataClass.Recommended

interface IRecommendedLoadCallback {
    fun onRecommendedLoadSuccess(recommendedList:List<Recommended>)
    fun onRecommendedLoadFailed(message:String)
}