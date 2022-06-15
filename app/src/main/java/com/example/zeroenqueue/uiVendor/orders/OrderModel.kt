package com.example.zeroenqueue.uiVendor.orders

import com.example.zeroenqueue.db.CartItem


class OrderModel {
    var key: String? = null
    var userId: String? = null
    var userName: String? = null
    var comment: String? = null
    var userPhone:String?=null
    var collectionTime: String?=null
    var transactionId: String? = null
    var totalPayment: Double = 0.toDouble()
    var finalPayment: Double = 0.toDouble()
    var isCod:Boolean = false
    var discount: Int = 0
    var cartItemList:List<CartItem> ?= null
    var orderNumber:String ?= null
    var orderStatus:Int = 0
    var createDate:Long =0
}