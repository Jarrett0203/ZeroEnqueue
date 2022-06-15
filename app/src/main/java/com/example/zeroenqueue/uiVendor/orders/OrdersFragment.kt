package com.example.zeroenqueue.uiVendor.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.VendorMyOrderAdapter
import com.example.zeroenqueue.databinding.FragmentOrdersBinding

class OrdersFragment : Fragment() {

//    private var _binding: FragmentOrdersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
//    private val binding get() = _binding!!

    lateinit var recycler_order: RecyclerView
    lateinit var layoutAnimationController: LayoutAnimationController
    lateinit var ordersViewModel: OrdersViewModel

    private var adapter: VendorMyOrderAdapter ?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


//        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
//        val root: View = binding.root
        val root = inflater.inflate(R.layout.fragment_orders, container, false)
        initView(root)

        ordersViewModel = ViewModelProvider(this).get(OrdersViewModel::class.java)

        ordersViewModel!!.messageError.observe(viewLifecycleOwner, Observer { s ->
            Toast.makeText(context, s , Toast.LENGTH_SHORT).show()
        })

        ordersViewModel!!.getOrderModelList().observe(viewLifecycleOwner, Observer { orderList ->
            if(orderList != null) {
                adapter = VendorMyOrderAdapter(requireContext(), orderList)
                recycler_order.adapter = adapter
                recycler_order.layoutAnimation = layoutAnimationController
            }
        })
//        val textView: TextView = binding.textGallery
//        ordersViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    private fun initView(root: View) {
        recycler_order = root.findViewById(R.id.recycler_order) as RecyclerView
        recycler_order.setHasFixedSize(true)
        recycler_order.layoutManager = LinearLayoutManager(context)

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.item_animation_from_left)
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}