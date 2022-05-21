package com.example.zeroenqueue.ui.categories

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.CategoryFoodListAdapter
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.FragmentCategoryFoodListBinding

class CategoryFoodListFragment : Fragment() {

    private var _binding: FragmentCategoryFoodListBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var recyclerViewFoodList: RecyclerView
    private lateinit var layoutAnimationController: LayoutAnimationController
    private var adapter : CategoryFoodListAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val categoryFoodListViewModel =
            ViewModelProvider(this).get(CategoryFoodListViewModel::class.java)

        _binding = FragmentCategoryFoodListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        recyclerViewFoodList = binding.recyclerFoodList
        initView()

        categoryFoodListViewModel.categoryFoodList.observe(viewLifecycleOwner) {
            adapter = CategoryFoodListAdapter(requireContext(), it)
            recyclerViewFoodList.adapter = adapter
            recyclerViewFoodList.layoutAnimation = layoutAnimationController
        }
        return root
    }

    private fun initView() {
        recyclerViewFoodList.setHasFixedSize(true)
        recyclerViewFoodList.layoutManager = LinearLayoutManager(context)
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)

        (activity as AppCompatActivity).supportActionBar!!.title = Common.categorySelected!!.name
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}