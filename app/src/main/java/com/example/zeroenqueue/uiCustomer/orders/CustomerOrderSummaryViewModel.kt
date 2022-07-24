package com.example.zeroenqueue.uiCustomer.orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zeroenqueue.classes.Order
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.interfaces.ILoadOrderCallbackListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CustomerOrderSummaryViewModel : ViewModel(), ILoadOrderCallbackListener {
    private var orderListMutableLiveData:MutableLiveData<List<Order>>? = null
    private lateinit var messageError:MutableLiveData<String>
    private var orderCallbackListener: ILoadOrderCallbackListener = this

    val orderList: LiveData<List<Order>>
        get() {
            if(orderListMutableLiveData == null){
                orderListMutableLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadOrderList()
            }
            return orderListMutableLiveData!!
        }

    fun loadOrderList() {
        val orderList = ArrayList<Order>()
        FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
            .orderByChild("userId")
            .equalTo(Common.currentUser!!.uid!!)
            .limitToLast(100)
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    orderCallbackListener.onLoadOrderFailed(error.message)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for(orderSnapShot in snapshot.children) {
                        val order = orderSnapShot.getValue(Order::class.java)
                        order!!.orderNumber = orderSnapShot.key
                        orderList.add(order)
                    }
                    orderCallbackListener.onLoadOrderSuccess(orderList)
                }
            })

    }

    override fun onLoadOrderSuccess(orderList: List<Order>) {
        orderListMutableLiveData!!.value = orderList
    }

    override fun onLoadOrderFailed(message: String) {
        messageError.value = message
    }
}
