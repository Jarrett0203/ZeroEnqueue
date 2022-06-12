package com.example.zeroenqueue.classes

import com.example.zeroenqueue.db.CartItem

class Order {
    var userId: String? = null
    var userName: String? = null
    var comment: String? = null
    var transactionId: String? = null
    var totalPayment: Double = 0.toDouble()
    var finalPayment: Double = 0.toDouble()
    var isCod:Boolean = false
    var discount: Int = 0
    var cartItemList:List<CartItem> ?= null
    var orderNumber:String ?= null
    var orderStatus:Int = 0
}