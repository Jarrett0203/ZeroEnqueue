package com.example.zeroenqueue.classes

import kotlin.String

class Food {
    var key: String? = null
    var name: String? = null
    var image: String? = null
    var id: String? = null
    var description: String? = null
    var price: Double = 0.00
    var addon:ArrayList<AddOn> = ArrayList()
    var size:ArrayList<Size> = ArrayList()
    var foodStall: String? = null
    var categories: String? = null

    var ratingValue:Double = 0.toDouble()
    var ratingCount:Long = 0.toLong()
    var addOnSelected: MutableList<AddOn>? = null
    var sizeSelected: Size? = null
}