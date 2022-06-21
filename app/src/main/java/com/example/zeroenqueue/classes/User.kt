package com.example.zeroenqueue.classes

class User {
    var image: String? = null
    var uid:String?= null
    var name:String?= null
    var email:String?= null
    var password:String?= null
    var address:String?= null
    var phone:String?= null
    var userType: String?= null
    var cardImage: String?= null
    var nus: Boolean = false
    var discounts: MutableList<Discount>?= ArrayList()
}