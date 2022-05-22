package com.example.zeroenqueue.common

import com.example.zeroenqueue.classes.Category
import com.example.zeroenqueue.classes.Food
import com.example.zeroenqueue.classes.User

object Common {
    var categorySelected: Category?=null
    var foodSelected: Food?=null
    var currentUser: User?= null

    val CATEGORY_REF: String ="Category"
    val POPULAR_REF: String ="MostPopular"
    val RECOMMENDED_REF: String ="Recommended"
    val USER_REF="Users"
    val COMMENT_REF = "Comments"

    val DATABASE_LINK: String ="https://zeroenqueue-default-rtdb.asia-southeast1.firebasedatabase.app/"
    val FULL_WIDTH_COLUMN: Int = 1
    val DEFAULT_COLUMN_COUNT: Int = 0

}