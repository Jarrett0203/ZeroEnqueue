package com.example.zeroenqueue.classes

class Food {
    var key: String? = null
    var name: String? = null
    var image: String? = null
    var id: String? = null
    var description: String? = null
    var price: Long = 0
    var addon:List<AddOn> = ArrayList<AddOn>()
    var size:List<Size> = ArrayList<Size>()

    var ratingValue:Float = 0.toFloat()
    var ratingCount:Long = 0.toLong()
}