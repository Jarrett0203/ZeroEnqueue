package com.example.zeroenqueue.model

class Food {
    var name: String? = null
    var image: String? = null
    var id: String? = null
    var description: String? = null
    var price: Long = 0
    var addon:List<AddOn> = ArrayList<AddOn>()
    var size:List<Size> = ArrayList<Size>()
}