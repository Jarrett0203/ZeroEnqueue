package com.example.zeroenqueue.ui.customerHome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asksira.loopingviewpager.LoopingViewPager
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.PopularCategoryAdapter
import com.example.zeroenqueue.adapters.RecommendedAdapter
import com.example.zeroenqueue.databinding.FragmentCustomerHomeBinding


class CustomerHomeFragment : Fragment() {

    private var _binding: FragmentCustomerHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewPager: LoopingViewPager
    private lateinit var layoutAnimationController: LayoutAnimationController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val customerHomeViewModel =
            ViewModelProvider(this)[CustomerHomeViewModel::class.java]

        _binding = FragmentCustomerHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = binding.recyclerPopular
        viewPager = binding.viewpager
        initView()

        customerHomeViewModel.popularList.observe(viewLifecycleOwner) {
            recyclerView.adapter = PopularCategoryAdapter(requireContext(), it)
            recyclerView.layoutAnimation = layoutAnimationController
        }
        customerHomeViewModel.recommendedList.observe(viewLifecycleOwner) {
            viewPager.adapter = RecommendedAdapter(it, true)
        }

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
        layoutAnimationController =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
    }
}