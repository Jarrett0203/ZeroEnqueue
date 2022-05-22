package com.example.zeroenqueue.interfaces

import com.example.zeroenqueue.classes.PopularCategory

interface IPopularLoadCallback {
    fun onPopularLoadSuccess(popularList:List<PopularCategory>)
    fun onPopularLoadFailed(message:String)
}