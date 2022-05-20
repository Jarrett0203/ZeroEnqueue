package com.example.zeroenqueue.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zeroenqueue.FoodItemAdapter
import com.example.zeroenqueue.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

private var _binding: FragmentHomeBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?): View {
    _binding = FragmentHomeBinding.inflate(inflater, container, false)
    val root: View = binding.root
    val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
    val recyclerView: RecyclerView = binding.recyclerViewHome

    homeViewModel.foodItems.observe(viewLifecycleOwner) {
      //implement recycler view
      recyclerView.layoutManager = LinearLayoutManager(activity)
      recyclerView.adapter = FoodItemAdapter(it)
    }

    return root
  }
override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

