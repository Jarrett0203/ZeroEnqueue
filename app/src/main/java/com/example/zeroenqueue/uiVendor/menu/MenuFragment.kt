package com.example.zeroenqueue.uiVendor.menu

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.VendorFoodListAdapter
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.common.SwipeHelper
import com.example.zeroenqueue.databinding.FragmentMenuBinding
import com.example.zeroenqueue.eventBus.MenuItemBack
import com.example.zeroenqueue.interfaces.IDeleteBtnCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.ChipGroup
import com.google.firebase.database.FirebaseDatabase
import dmax.dialog.SpotsDialog
import org.greenrobot.eventbus.EventBus

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var dialog: AlertDialog
    private var adapter : VendorFoodListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val menuViewModel =
            ViewModelProvider(this)[MenuViewModel::class.java]

        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment_content_main_vendor)

        val recyclerViewMenuList = binding.recyclerMenuList
        val swipeRefreshLayout = binding.swipeRefresh
        val addNewFood = binding.addNewFood
        val fabFilter = binding.filterFab


        dialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()
        dialog.show()
        recyclerViewMenuList.setHasFixedSize(true)
        recyclerViewMenuList.layoutManager = LinearLayoutManager(context)
        val layoutAnimationController =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)

        val filterBottomSheetDialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
        val layoutFilter = layoutInflater.inflate(R.layout.layout_filter_food, null)
        val chipGroupCategory = layoutFilter.findViewById(R.id.layout_chip_group_category) as ChipGroup
        val btnFilter = layoutFilter.findViewById(R.id.btnFilter) as Button
        filterBottomSheetDialog.setContentView(layoutFilter)

        swipeRefreshLayout.setOnRefreshListener {
            menuViewModel.loadMenuList()
            swipeRefreshLayout.isRefreshing = false
        }

        addNewFood.setOnClickListener {
            navController.navigate(R.id.navigation_vendorFoodDetail)
        }

        fabFilter.setOnClickListener {

        }

        btnFilter.setOnClickListener {

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

        val swipe = object : SwipeHelper(requireContext(), recyclerViewMenuList, width/5) {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {
                buffer.add(
                    MyButton(requireContext(), "Delete", 30, 0, Color.parseColor("#FF3c30"),
                        object : IDeleteBtnCallback {
                            override fun onClick(pos: Int) {
                                val foodItem = adapter!!.getItemAtPosition(pos)
                                val builder = AlertDialog.Builder(requireContext())
                                    .setTitle("Delete")
                                    .setMessage("Do you really want to delete this order?")
                                    .setNegativeButton("CANCEL") { dialogInterface, _ ->
                                        dialogInterface.dismiss()
                                    }
                                    .setPositiveButton("DELETE") { dialogInterface, _ ->
                                        FirebaseDatabase.getInstance()
                                            .getReference(Common.FOODLIST_REF)
                                            .child(foodItem.id!!)
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
                                                    "Food item has been deleted",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                menuViewModel.loadMenuList()
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

        menuViewModel.menuList.observe(viewLifecycleOwner) {
            dialog.dismiss()
            adapter = VendorFoodListAdapter(requireContext(), it.toMutableList())
            recyclerViewMenuList.adapter = adapter
            recyclerViewMenuList.layoutAnimation = layoutAnimationController
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        EventBus.getDefault().postSticky(MenuItemBack())
        super.onDestroy()
    }
}