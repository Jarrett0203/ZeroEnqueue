package com.example.zeroenqueue.ui.foodStall

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.FoodStallAdapter
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.common.SpacesItemDecoration
import com.example.zeroenqueue.databinding.FragmentFoodStallBinding
import dmax.dialog.SpotsDialog

class FoodStallFragment : Fragment() {

    private var _binding: FragmentFoodStallBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var recyclerViewFoodStall: RecyclerView
    private lateinit var dialog: AlertDialog
    private lateinit var layoutAnimationController: LayoutAnimationController
    private var adapter: FoodStallAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val foodStallViewModel =
            ViewModelProvider(this).get(FoodStallViewModel::class.java)

        _binding = FragmentFoodStallBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerViewFoodStall = binding.recyclerFoodStalls
        initView()

        foodStallViewModel.errorMessage.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
        foodStallViewModel.foodStallList.observe(viewLifecycleOwner) {
            dialog.dismiss()
            adapter = FoodStallAdapter(requireContext(), it)
            recyclerViewFoodStall.adapter = adapter
            recyclerViewFoodStall.layoutAnimation = layoutAnimationController
        }
        return root
    }

    private fun initView() {
        dialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()
        dialog.show()
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)
        recyclerViewFoodStall.setHasFixedSize(true)
        recyclerViewFoodStall.layoutManager = LinearLayoutManager(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}