package com.example.zeroenqueue.uiVendor.vendorDiscountsDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zeroenqueue.classes.Discount
import com.example.zeroenqueue.common.Common

class VendorDiscountsDetailViewModel : ViewModel() {
    private var discountDetailMutableLiveData: MutableLiveData<Discount>? = null

    val discountDetail: LiveData<Discount>
        get() {
            if (discountDetailMutableLiveData == null) {
                discountDetailMutableLiveData = MutableLiveData()
            }
            discountDetailMutableLiveData!!.value = Common.discountSelected
            return discountDetailMutableLiveData!!
        }
}