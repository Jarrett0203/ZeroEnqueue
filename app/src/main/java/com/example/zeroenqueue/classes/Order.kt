package com.example.zeroenqueue.classes

import android.widget.EditText
import android.widget.TextView
import com.example.zeroenqueue.db.CartItem

class Order {
    var userId: String? = null
    var userName: String? = null
    var comment: String? = null
    var userPhone:String?=null
    var collectionTime: EditText?=null
    var transactionId: String? = null
    var totalPayment: TextView?=null
    var finalPayment: TextView?=null
    var isCod:Boolean = false
    var discount: Int = 0
    var cartItemList:List<CartItem> ?= null
    var orderNumber:String ?= null
    var orderStatus:Int = 0
    var createDate:Long?=0
}