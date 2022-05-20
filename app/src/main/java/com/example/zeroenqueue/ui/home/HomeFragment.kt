package com.example.zeroenqueue.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asksira.loopingviewpager.LoopingViewPager
import com.example.zeroenqueue.adapters.PopularCategoryAdapter
import com.example.zeroenqueue.adapters.RecommendedAdapter
import com.example.zeroenqueue.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewPager: LoopingViewPager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        recyclerView = binding.recyclerPopular
        viewPager = binding.viewpager
        initView()
        homeViewModel.popularList.observe(viewLifecycleOwner) {
            recyclerView.adapter = PopularCategoryAdapter(requireContext(), it)
        }
        homeViewModel.recommendedList.observe(viewLifecycleOwner){
            viewPager.adapter = RecommendedAdapter(it, false)
        }

        /*val recyclerView: RecyclerView = binding.recyclerViewHome

        homeViewModel.foodItems.observe(viewLifecycleOwner) {
          //implement recycler view
          recyclerView.layoutManager = LinearLayoutManager(activity)
          recyclerView.adapter = FoodItemAdapter(it)
        }*/

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        viewPager.resumeAutoScroll()
    }

    override fun onPause() {
        viewPager.pauseAutoScroll()
        super.onPause()
    }

    private fun initView() {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    }
}

