package com.example.zeroenqueue.classes

class Category {
    var foods: List<Food>?=null
    var menu_id: String?=null
    var name: String?=null
    var image: String?=null

    constructor()
    constructor(foods:List<Food>?, menu_id:String?, name:String?, image:String?) {
        this.foods = foods
        this.menu_id = menu_id
        this.name = name
        this.image = image
    }
}