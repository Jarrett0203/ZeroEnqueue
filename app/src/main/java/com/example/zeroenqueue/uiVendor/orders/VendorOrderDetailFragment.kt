package com.example.zeroenqueue.uiVendor.orders

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.OrderDetailAdapter
import com.example.zeroenqueue.databinding.FragmentCustomerOrderDetailBinding
import com.example.zeroenqueue.databinding.FragmentVendorOrderDetailBinding
import com.example.zeroenqueue.uiCustomer.orders.CustomerOrderDetailViewModel

class VendorOrderDetailFragment : Fragment() {

    private var _binding: FragmentVendorOrderDetailBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val vendorOrderDetailViewModel = ViewModelProvider(this)[VendorOrderDetailViewModel::class.java]
        _binding = FragmentVendorOrderDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val recycler_order_detail = binding.recyclerOrderDetail
        recycler_order_detail.setHasFixedSize(true)
        recycler_order_detail.layoutManager = LinearLayoutManager(context)
        val layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)
        vendorOrderDetailViewModel.vendorOrderDetail.observe(viewLifecycleOwner) {
            if (it.isEmpty() || it == null) {
                recycler_order_detail.visibility = View.GONE
            }
            else {
                recycler_order_detail.adapter = OrderDetailAdapter(requireContext(), it)
                recycler_order_detail.layoutAnimation = layoutAnimationController
            }
        }
        return root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}