package com.example.zeroenqueue.interfaces

import com.example.zeroenqueue.classes.Category

interface ICategoryLoadCallback {
    fun onCategoryLoadSuccess(categoryList:List<Category>)
    fun onCategoryLoadFailed(message:String)
}