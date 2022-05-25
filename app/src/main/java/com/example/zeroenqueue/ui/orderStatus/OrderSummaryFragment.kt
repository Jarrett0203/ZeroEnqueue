package com.example.zeroenqueue.ui.orderStatus

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zeroenqueue.R
//import com.example.zeroenqueue.adapters.MyOrderAdapter
import com.example.zeroenqueue.interfaces.ILoadOrderCallbackListener
import com.example.zeroenqueue.classes.Order
import com.example.zeroenqueue.common.Common
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import dmax.dialog.SpotsDialog
import java.util.*
import kotlin.collections.ArrayList

class OrderSummaryFragment : Fragment(), ILoadOrderCallbackListener {
    private var orderSummaryModel: OrderSummaryModel?=null

    internal lateinit var dialog: AlertDialog

    private lateinit var recycler_order:RecyclerView

    internal lateinit var listener:ILoadOrderCallbackListener

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        orderSummaryModel = ViewModelProvider.get(OrderSummaryModel::class.java)
//        val root = inflater.inflate(R.layout.fragment_order_summary, container, false)
//        initViews(root)
//        loadOrderFromFirebase()
//
//        orderSummaryModel!!.mutableLiveDataOrderList.observe(this, Observer {
//            Collections.reverse(it!!)
//            val adapter = MyOrderAdapter(context!!, it!!)
//            recycler_order!!.adapter = adapter
//        })
//
//        return root
//    }

    private fun loadOrderFromFirebase() {
        dialog.show()
        val orderList = ArrayList<Order>()

            FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
                .orderByChild("userId")
                .equalTo(Common.currentUser!!.uid!!)
                .limitToLast(100)
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        listener.onLoadOrderFailed(error.message!!)
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(orderSnapShot in snapshot.children) {
                            val order = orderSnapShot.getValue(Order::class.java)
                            order!!.orderNumber = orderSnapShot.key
                            orderList.add(order!!)
                        }
                        listener.onLoadOrderSuccess(orderList)
                    }
                }
        )
    }

    private fun initViews(root: View?) {
        listener = this
        dialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()
        recycler_order.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recycler_order.layoutManager = layoutManager
        recycler_order.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))
    }
    override fun onLoadOrderSuccess(orderList: List<Order>) {

    }

    override fun onLoadOrderFailed(message: String) {
        dialog.dismiss()
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


}