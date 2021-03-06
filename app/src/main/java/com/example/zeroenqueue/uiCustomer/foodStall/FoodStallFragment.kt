package com.example.zeroenqueue.uiCustomer.foodStall

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.FoodStallAdapter
import com.example.zeroenqueue.databinding.FragmentFoodStallBinding
import com.example.zeroenqueue.eventBus.MenuItemBack
import dmax.dialog.SpotsDialog
import org.greenrobot.eventbus.EventBus

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
            ViewModelProvider(this)[FoodStallViewModel::class.java]

        _binding = FragmentFoodStallBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerViewFoodStall = binding.recyclerFoodStalls
        dialog = SpotsDialog.Builder().setContext(context).setCancelable(true).build()
        dialog.show()
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)
        recyclerViewFoodStall.setHasFixedSize(true)
        recyclerViewFoodStall.layoutManager = LinearLayoutManager(context)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        EventBus.getDefault().postSticky(MenuItemBack())
        super.onDestroy()
    }

}