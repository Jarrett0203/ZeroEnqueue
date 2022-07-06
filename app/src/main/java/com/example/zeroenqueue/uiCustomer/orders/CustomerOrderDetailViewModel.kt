package com.example.zeroenqueue.uiCustomer.orders

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zeroenqueue.classes.Food
import com.example.zeroenqueue.classes.Order
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.db.CartItem
import com.example.zeroenqueue.interfaces.ICustomerOrderDetailCallBack
import com.example.zeroenqueue.interfaces.IFoodLoadCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CustomerOrderDetailViewModel : ViewModel(), ICustomerOrderDetailCallBack {
    private lateinit var messageError:MutableLiveData<kotlin.String>
    private var customerOrderDetailCallBackListener : ICustomerOrderDetailCallBack = this

    private var customerOrderDetailLiveData: MutableLiveData<List<CartItem>>?=null

    val customerOrderDetail:LiveData<List<CartItem>>
        get(){
            if(customerOrderDetailLiveData == null){
                customerOrderDetailLiveData = MutableLiveData()
                loadCartItemList()
            }
            return customerOrderDetailLiveData!!
        }

    private fun loadCartItemList() {
        var tempList = ArrayList<CartItem>()
        val orderRef = FirebaseDatabase.getInstance(Common.DATABASE_LINK).getReference(Common.ORDER_REF)
        orderRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(itemSnapShot in snapshot.children){
                    val order = itemSnapShot.getValue(Order::class.java)
                    if (order!!.foodStallId == Common.foodStallSelected!!.id) {
                        tempList = order.cartItemList as ArrayList<CartItem>
                        break
                    }
                }
                customerOrderDetailCallBackListener.onCustomerOrderDetailLoadSuccess(tempList)
            }

            override fun onCancelled(error: DatabaseError) {
                customerOrderDetailCallBackListener.onCustomerOrderDetailLoadFailed(error.message)
            }
        })
    }

    override fun onCustomerOrderDetailLoadSuccess(cartItemList: List<CartItem>) {
        customerOrderDetailLiveData!!.value = cartItemList
    }

    override fun onCustomerOrderDetailLoadFailed(message: String) {
        messageError.value = message
    }
}