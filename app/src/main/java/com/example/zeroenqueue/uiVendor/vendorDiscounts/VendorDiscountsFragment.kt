package com.example.zeroenqueue.uiVendor.vendorDiscounts

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Rect
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.CustomerDiscountsAdapter
import com.example.zeroenqueue.adapters.VendorDiscountsAdapter
import com.example.zeroenqueue.adapters.VendorFoodListAdapter
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.common.SwipeHelper
import com.example.zeroenqueue.databinding.FragmentVendorDiscountsBinding
import com.example.zeroenqueue.interfaces.IDeleteBtnCallback
import com.google.firebase.database.FirebaseDatabase
import dmax.dialog.SpotsDialog

class VendorDiscountsFragment : Fragment() {

    private var _binding: FragmentVendorDiscountsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var adapter : VendorDiscountsAdapter? = null

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

        val displayMetrics = DisplayMetrics()
        val version = android.os.Build.VERSION.SDK_INT
        var width = 0
        if (version >= android.os.Build.VERSION_CODES.S) {
            val windowMetrics = requireActivity().windowManager.currentWindowMetrics
            val bounds: Rect = windowMetrics.bounds
            width = bounds.width()
        }
        if (version == android.os.Build.VERSION_CODES.R) {
            @Suppress("DEPRECATION")
            requireActivity().display?.getRealMetrics(displayMetrics)
            width = displayMetrics.widthPixels
        }
        if (version < android.os.Build.VERSION_CODES.R) {
            @Suppress("DEPRECATION")
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            width = displayMetrics.widthPixels
        }

        val swipe = object : SwipeHelper(requireContext(), recyclerViewDiscounts, width/5) {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {
                buffer.add(
                    MyButton(requireContext(), "Delete", 30, 0, Color.parseColor("#FF3c30"),
                        object : IDeleteBtnCallback {
                            override fun onClick(pos: Int) {
                                val discount = adapter!!.getItemAtPosition(pos)
                                val builder = AlertDialog.Builder(requireContext())
                                    .setTitle("Delete")
                                    .setMessage("Do you really want to delete this order?")
                                    .setNegativeButton("CANCEL") { dialogInterface, _ ->
                                        dialogInterface.dismiss()
                                    }
                                    .setPositiveButton("DELETE") { dialogInterface, _ ->
                                        FirebaseDatabase.getInstance()
                                            .getReference(Common.DISCOUNT_REF)
                                            .child(discount.id!!)
                                            .removeValue()
                                            .addOnFailureListener {
                                                Toast.makeText(
                                                    requireContext(),
                                                    it.message,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            .addOnSuccessListener {
                                                adapter!!.removeItem(pos)
                                                adapter!!.notifyItemRemoved(pos)
                                                dialogInterface.dismiss()
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Discount has been deleted",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                vendorDiscountsViewModel.loadDiscountList()
                                            }
                                    }

                                val dialog = builder.create()
                                dialog.show()

                                val btnNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                                btnNegative.setTextColor(Color.LTGRAY)
                                val btnPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                                btnPositive.setTextColor(Color.RED)
                            }
                        })
                )
            }
        }


        vendorDiscountsViewModel.discountList.observe(viewLifecycleOwner) {
            if (it == null || it.isEmpty()) {
                recyclerViewDiscounts.visibility = View.GONE
                noDiscount.visibility = View.VISIBLE
            } else {
                adapter = VendorDiscountsAdapter(requireContext(), it.toMutableList())
                recyclerViewDiscounts.adapter = adapter
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