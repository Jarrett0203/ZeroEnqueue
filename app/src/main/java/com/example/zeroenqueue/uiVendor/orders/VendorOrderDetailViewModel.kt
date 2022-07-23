package com.example.zeroenqueue.uiVendor.orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zeroenqueue.classes.Order
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.db.CartItem
import com.example.zeroenqueue.interfaces.IOrderDetailCallBack
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VendorOrderDetailViewModel : ViewModel(), IOrderDetailCallBack {
    private lateinit var messageError: MutableLiveData<String>
    private var vendorOrderDetailCallBackListener : IOrderDetailCallBack = this

    private var vendorOrderDetailLiveData: MutableLiveData<List<CartItem>>?=null

    val vendorOrderDetail: LiveData<List<CartItem>>
        get(){
            if(vendorOrderDetailLiveData == null){
                vendorOrderDetailLiveData = MutableLiveData()
                loadCartItemList()
            }
            return vendorOrderDetailLiveData!!
        }

    private fun loadCartItemList() {
        var tempList = ArrayList<CartItem>()
        val orderRef = FirebaseDatabase.getInstance(Common.DATABASE_LINK).getReference(Common.ORDER_REF)
        orderRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(itemSnapShot in snapshot.children){
                    val order = itemSnapShot.getValue(Order::class.java)
                    if (order!!.createDate == Common.orderSelected!!.createDate) {
                        tempList = order.cartItemList as ArrayList<CartItem>
                        break
                    }
                }
                vendorOrderDetailCallBackListener.onCustomerOrderDetailLoadSuccess(tempList)
            }

            override fun onCancelled(error: DatabaseError) {
                vendorOrderDetailCallBackListener.onCustomerOrderDetailLoadFailed(error.message)
            }
        })
    }

    override fun onCustomerOrderDetailLoadSuccess(cartItemList: List<CartItem>) {
        vendorOrderDetailLiveData!!.value = cartItemList
    }

    override fun onCustomerOrderDetailLoadFailed(message: String) {
        messageError.value = message
    }
}