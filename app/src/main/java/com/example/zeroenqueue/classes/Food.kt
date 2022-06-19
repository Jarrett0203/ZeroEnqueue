package com.example.zeroenqueue.classes

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

    var ratingValue:Float = 0.toFloat()
    var ratingCount:Long = 0.toLong()
    var addOnSelected: MutableList<AddOn>? = null
    var sizeSelected: Size? = null
}