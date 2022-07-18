package com.example.zeroenqueue.uiVendor.addNewDiscount

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.AddNewDiscountAdapter
import com.example.zeroenqueue.adapters.VendorDiscountsAdapter
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.FragmentAddNewDiscountBinding
import com.example.zeroenqueue.databinding.FragmentVendorOrderSummaryBinding
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.fragment_add_new_discount.*

class AddNewDiscountFragment : Fragment() {

    private var _binding: FragmentAddNewDiscountBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val addNewDiscountViewModel = ViewModelProvider(this)[AddNewDiscountViewModel::class.java]
        _binding = FragmentAddNewDiscountBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerFoodList = binding.recyclerFoodList
        val swipeRefreshLayout = binding.swipeRefresh
        val noFoodList = binding.noFoodListLayout
        val btnAddNewDiscount = binding.btnAddNewDiscount

        val navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment_content_main_vendor)

        val dialog: AlertDialog =
            SpotsDialog.Builder().setContext(context).setCancelable(false).build()
        dialog.show()
        recyclerFoodList.setHasFixedSize(true)
        recyclerFoodList.layoutManager = LinearLayoutManager(context)
        val layoutAnimationController: LayoutAnimationController =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)

        swipeRefreshLayout.setOnRefreshListener {
            addNewDiscountViewModel.loadFoodList()
            swipeRefreshLayout.isRefreshing = false
        }

        btnAddNewDiscount.setOnClickListener {
            if (Common.foodSelected == null)
                Toast.makeText(requireContext(), "Please select a food item", Toast.LENGTH_SHORT).show()
            else
                navController.navigate(R.id.navigation_vendor_discounts_detail)
        }

        addNewDiscountViewModel.foodList.observe(viewLifecycleOwner) {
            if (it == null || it.isEmpty()) {
                recyclerFoodList.visibility = View.GONE
                noFoodList.visibility = View.VISIBLE
            } else {
                recyclerFoodList.visibility = View.VISIBLE
                noFoodList.visibility = View.GONE
                recyclerFoodList.adapter = AddNewDiscountAdapter(requireContext(), it)
                recyclerFoodList.layoutAnimation = layoutAnimationController
            }
            dialog.dismiss()
        }
        return root

    }

}