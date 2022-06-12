package com.example.zeroenqueue.uiVendor.vendorHome

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.VendorFoodListAdapter
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.FragmentVendorHomeBinding
import dmax.dialog.SpotsDialog

class VendorHomeFragment : Fragment() {

    private var _binding: FragmentVendorHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var dialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val vendorHomeViewModel =
            ViewModelProvider(this)[VendorHomeViewModel::class.java]

        _binding = FragmentVendorHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val swipeRefreshLayout = binding.swipeRefresh

        val stallName = binding.stallName
        val stallAddress = binding.stallAddress

        stallName.text = Common.foodStallSelected!!.name
        stallAddress.text = Common.foodStallSelected!!.address

        //implement when orders are set up
        val totalSales = binding.totalSales
        val currentOrders = binding.currentOrders

        val recyclerViewPopularFood = binding.recyclerPopular
        val filterPopularFood = binding.filterPopularFood

        filterPopularFood.setOnClickListener {

        }

        dialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()
        dialog.show()
        recyclerViewPopularFood.setHasFixedSize(true)
        recyclerViewPopularFood.layoutManager = LinearLayoutManager(context)
        val layoutAnimationController =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)

        vendorHomeViewModel.foodList.observe(viewLifecycleOwner) {
            dialog.dismiss()
            recyclerViewPopularFood.adapter = VendorFoodListAdapter(requireContext(), it)
            recyclerViewPopularFood.layoutAnimation = layoutAnimationController
        }

        swipeRefreshLayout.setOnRefreshListener {
            vendorHomeViewModel.loadFoodList()
            swipeRefreshLayout.isRefreshing = false
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}