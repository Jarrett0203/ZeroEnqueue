package com.example.zeroenqueue.interfaces

import com.example.zeroenqueue.classes.Order

interface ILoadTimeFromFirebaseCallback {
    fun onLoadTimeSuccess(order: Order, estimatedTimeMs:Long)
    fun onLoadTimeFailed(message:String)
} 