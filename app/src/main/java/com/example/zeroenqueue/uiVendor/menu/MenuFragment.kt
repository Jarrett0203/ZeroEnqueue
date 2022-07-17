package com.example.zeroenqueue.uiVendor.menu

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.VendorFoodListAdapter
import com.example.zeroenqueue.databinding.FragmentMenuBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.ChipGroup
import dmax.dialog.SpotsDialog

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var dialog: AlertDialog

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

        menuViewModel.menuList.observe(viewLifecycleOwner) {
            dialog.dismiss()
            recyclerViewMenuList.adapter = VendorFoodListAdapter(requireContext(), it)
            recyclerViewMenuList.layoutAnimation = layoutAnimationController
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}