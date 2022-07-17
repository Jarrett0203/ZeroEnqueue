package com.example.zeroenqueue.uiVendor.vendorDiscounts

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.CustomerDiscountsAdapter
import com.example.zeroenqueue.adapters.VendorDiscountsAdapter
import com.example.zeroenqueue.databinding.FragmentVendorDiscountsBinding
import dmax.dialog.SpotsDialog

class VendorDiscountsFragment : Fragment() {

    private var _binding: FragmentVendorDiscountsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val vendorDiscountsViewModel = ViewModelProvider(this)[VendorDiscountsViewModel::class.java]

        _binding = FragmentVendorDiscountsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment_content_main_vendor)

        val recyclerViewDiscounts = binding.recyclerDiscounts
        val swipeRefreshLayout = binding.swipeRefresh
        val noDiscount = binding.noDiscounts
        val addNewDiscounts = binding.addNewDiscounts

        val dialog: AlertDialog =
            SpotsDialog.Builder().setContext(context).setCancelable(false).build()
        dialog.show()
        recyclerViewDiscounts.setHasFixedSize(true)
        recyclerViewDiscounts.layoutManager = LinearLayoutManager(context)
        val layoutAnimationController: LayoutAnimationController =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)

        swipeRefreshLayout.setOnRefreshListener {
            vendorDiscountsViewModel.loadDiscountList()
            swipeRefreshLayout.isRefreshing = false
        }

        addNewDiscounts.setOnClickListener {
            navController.navigate(R.id.navigation_add_new_discount)
        }

        vendorDiscountsViewModel.discountList.observe(viewLifecycleOwner) {
            if (it == null || it.isEmpty()) {
                recyclerViewDiscounts.visibility = View.GONE
                noDiscount.visibility = View.VISIBLE
            } else {
                recyclerViewDiscounts.adapter = VendorDiscountsAdapter(requireContext(), it)
                recyclerViewDiscounts.layoutAnimation = layoutAnimationController
            }
            dialog.dismiss()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}