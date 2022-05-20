package com.example.zeroenqueue.callback

import com.example.zeroenqueue.dataClass.PopularCategory

interface IPopularLoadCallback {
    fun onPopularLoadSuccess(popularList:List<PopularCategory>)
    fun onPopularLoadFailed(message:String)
}