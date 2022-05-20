package com.example.zeroenqueue.callback

import com.example.zeroenqueue.model.PopularCategory

interface IPopularLoadCallback {
    fun onPopularLoadSuccess(popularList:List<PopularCategory>)
    fun onPopularLoadFailed(message:String)
}