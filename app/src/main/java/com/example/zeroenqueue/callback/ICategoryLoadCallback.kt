package com.example.zeroenqueue.callback

import com.example.zeroenqueue.model.Category

interface ICategoryLoadCallback {
    fun onCategoryLoadSuccess(categoryList:List<Category>)
    fun onCategoryLoadFailed(message:String)
}