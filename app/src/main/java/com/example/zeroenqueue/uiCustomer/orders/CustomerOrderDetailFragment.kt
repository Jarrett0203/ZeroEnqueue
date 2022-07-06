package com.example.zeroenqueue.uiCustomer.orders

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zeroenqueue.R
import com.example.zeroenqueue.databinding.FragmentCustomerOrderDetailBinding
import com.example.zeroenqueue.databinding.FragmentFoodListBinding

class CustomerOrderDetailFragment : Fragment() {

    private var _binding: FragmentCustomerOrderDetailBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val customerOrderDetailViewModel = ViewModelProvider(this)[CustomerOrderDetailViewModel::class.java]
        _binding = FragmentCustomerOrderDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val recycler_order_detail = binding.recyclerOrderDetail
        recycler_order_detail.setHasFixedSize(true)
        recycler_order_detail.layoutManager = LinearLayoutManager(context)
        val layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)
        customerOrderDetailViewModel.customerOrderDetail.observe(viewLifecycleOwner) {
            //recycler_order_detail.adapter = OrderDetailAdapter(requireContext(), it)
            recycler_order_detail.layoutAnimation = layoutAnimationController
        }

        return root

    }


}