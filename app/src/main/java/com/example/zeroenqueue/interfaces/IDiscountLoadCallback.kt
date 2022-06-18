package com.example.zeroenqueue.interfaces

import com.example.zeroenqueue.classes.Discount

interface IDiscountLoadCallback {
    fun onDiscountLoadSuccess(discountList:List<Discount>)
    fun onDiscountLoadFailed(message:String)
}
