package com.example.zeroenqueue.uiCustomer.orders

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zeroenqueue.adapters.MyOrderAdapter
import com.example.zeroenqueue.interfaces.ILoadOrderCallbackListener
import com.example.zeroenqueue.classes.Order
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.FragmentCustomerOrderSummaryBinding
import com.example.zeroenqueue.eventBus.MenuItemBack
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dmax.dialog.SpotsDialog
import org.greenrobot.eventbus.EventBus
import java.util.*
import kotlin.collections.ArrayList

class CustomerOrderSummaryFragment : Fragment(){
    private var _binding: FragmentCustomerOrderSummaryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var customerOrderSummaryViewModel: CustomerOrderSummaryViewModel?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        customerOrderSummaryViewModel = ViewModelProvider(this)[CustomerOrderSummaryViewModel::class.java]
        _binding = FragmentCustomerOrderSummaryBinding.inflate(inflater, container, false)
        val root = binding.root

        val dialog : AlertDialog = SpotsDialog.Builder().setContext(requireContext()).setCancelable(false).build()
        dialog.show()
        val noOrders = binding.txtNoOrders
        val recycler_order = binding.recyclerOrder
        val swipeRefresh = binding.swipeRefresh
        recycler_order.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recycler_order.layoutManager = layoutManager
        recycler_order.addItemDecoration(DividerItemDecoration(requireContext(), layoutManager.orientation))

        swipeRefresh.setOnRefreshListener {
            customerOrderSummaryViewModel!!.loadOrderList()
            swipeRefresh.isRefreshing = false
        }

        customerOrderSummaryViewModel!!.orderList.observe(viewLifecycleOwner) {
            dialog.dismiss()
            if (it.isEmpty() || it == null) {
                recycler_order.visibility = View.GONE
                noOrders.visibility = View.VISIBLE
            }
            else {
                Collections.reverse(it)
                val adapter = MyOrderAdapter(requireContext(), it)
                recycler_order.adapter = adapter
            }
        }

        return root
    }

    override fun onDestroy() {
        EventBus.getDefault().postSticky(MenuItemBack())
        super.onDestroy()
    }


}