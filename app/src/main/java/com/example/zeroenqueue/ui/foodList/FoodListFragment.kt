package com.example.zeroenqueue.ui.foodList

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.FoodListAdapter
import com.example.zeroenqueue.adapters.PopularCategoryAdapter
import com.example.zeroenqueue.databinding.FragmentFoodListBinding
import com.example.zeroenqueue.databinding.FragmentFoodStallBinding
import com.example.zeroenqueue.ui.foodStall.FoodStallViewModel
import dmax.dialog.SpotsDialog

class FoodListFragment : Fragment() {

    private var _binding: FragmentFoodListBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var recyclerViewFoodList: RecyclerView
    private lateinit var layoutAnimationController: LayoutAnimationController
    private lateinit var dialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val foodListViewModel =
            ViewModelProvider(this).get(FoodListViewModel::class.java)

        _binding = FragmentFoodListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        recyclerViewFoodList = binding.recyclerFoodList
        initView()

        foodListViewModel.foodList.observe(viewLifecycleOwner) {
            dialog.dismiss()
            recyclerViewFoodList.adapter = FoodListAdapter(requireContext(), it)
            recyclerViewFoodList.layoutAnimation = layoutAnimationController
        }
        return root
    }

    private fun initView() {
        dialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()
        dialog.show()
        layoutAnimationController =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)
        recyclerViewFoodList.setHasFixedSize(true)
        recyclerViewFoodList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}