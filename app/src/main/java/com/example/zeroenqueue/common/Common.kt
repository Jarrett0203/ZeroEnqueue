package com.example.zeroenqueue.common

import com.example.zeroenqueue.model.Category
import com.example.zeroenqueue.model.Food

object Common {
    var categorySelected: Category?=null
    var foodSelected: Food?=null
    val CATEGORY_REF: String ="Category"
    val POPULAR_REF: String ="MostPopular"
    val RECOMMENDED_REF: String ="Recommended"
    val USER_REF="Users"
    val DATABASE_LINK: String ="https://zeroenqueue-default-rtdb.asia-southeast1.firebasedatabase.app/"
    val FULL_WIDTH_COLUMN: Int = 1
    val DEFAULT_COLUMN_COUNT: Int = 0

}