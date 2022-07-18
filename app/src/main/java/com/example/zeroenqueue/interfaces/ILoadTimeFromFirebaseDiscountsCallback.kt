package com.example.zeroenqueue.interfaces

import com.example.zeroenqueue.classes.Discount

interface ILoadTimeFromFirebaseDiscountsCallback {
    fun onLoadTimeSuccess(discount: Discount, estimatedTimeMs:Long)
    fun onLoadTimeFailed(message:String)
} 