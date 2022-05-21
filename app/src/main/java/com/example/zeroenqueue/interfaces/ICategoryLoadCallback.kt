package com.example.zeroenqueue.interfaces

import com.example.zeroenqueue.model.Category

interface ICategoryLoadCallback {
    fun onCategoryLoadSuccess(categoryList:List<Category>)
    fun onCategoryLoadFailed(message:String)
}