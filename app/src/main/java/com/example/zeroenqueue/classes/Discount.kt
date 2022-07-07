package com.example.zeroenqueue.classes

import kotlin.String

class Discount {
    var key: String? = null
    var id: String? = null
    var name: String? = null
    var discount: Int = 0
    var expiry: Long = 0
    var isExpired: Boolean = false
    var isRedeemed: Boolean = false
    var description: String? = null
    var foodUid: String? = null
    var foodName: String? = null
    var foodImage: String? = null
    var foodStallUid: String? = null
    var foodStallName: String? = null
    var oldPrice: Double = 0.0
    var newPrice: Double = 0.0
}