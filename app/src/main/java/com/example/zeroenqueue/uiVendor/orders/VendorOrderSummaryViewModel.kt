package com.example.zeroenqueue.uiVendor.orders

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zeroenqueue.classes.Order
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.interfaces.IVendorOrderCallbackListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class VendorOrderSummaryViewModel : ViewModel(), IVendorOrderCallbackListener {

    private val orderList = MutableLiveData<MutableList<Order>>()
        val messageError = MutableLiveData<String>()
        private val orderCallbackListener: IVendorOrderCallbackListener

        init {
            orderCallbackListener = this
        }

        fun getOrderList(): MutableLiveData<MutableList<Order>>{
            loadAllOrders()
            return orderList
        }

        fun loadAllOrders() {
            val tempList : MutableList<Order> = ArrayList()
            val orderRef = FirebaseDatabase.getInstance()
                .getReference(Common.ORDER_REF)
            orderRef.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    orderCallbackListener.onOrderLoadFailed(error.message)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for(itemSnapShot in snapshot.children) {
                        val order = itemSnapShot.getValue(Order::class.java)
                        if (order!!.foodStallId == Common.foodStallSelected!!.id) {
                            order.key = itemSnapShot.key
                            tempList.add(order)
                        }

                    }
                    orderCallbackListener.onOrderLoadSuccess(tempList)
                }
            })
        }

        fun loadOrder(status: Int) {
            val tempList : MutableList<Order> = ArrayList()
            val orderRef = FirebaseDatabase.getInstance()
                .getReference(Common.ORDER_REF)
                .orderByChild("orderStatus")
                .equalTo(status.toDouble())
            orderRef.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    orderCallbackListener.onOrderLoadFailed(error.message)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for(itemSnapShot in snapshot.children) {
                        val order = itemSnapShot.getValue(Order::class.java)
                        if (order!!.foodStallId == Common.foodStallSelected!!.id) {
                            order.key = itemSnapShot.key
                            tempList.add(order)
                        }

                    }
                    orderCallbackListener.onOrderLoadSuccess(tempList)
                }
            })

        }

    override fun onOrderLoadSuccess(order: MutableList<Order>) {
        if(order.size >= 0) {
            Collections.sort(order) { t1, t2 ->
                if(t1.createDate < t2.createDate) return@sort -1
                if(t1.createDate == t2.createDate) 0 else 1
            }
            orderList.value = order
        }

    }

    override fun onOrderLoadFailed(message: String) {
        messageError.value = message
    }
}