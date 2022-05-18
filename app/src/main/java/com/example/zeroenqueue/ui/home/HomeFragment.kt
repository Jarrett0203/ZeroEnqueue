package com.example.zeroenqueue.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zeroenqueue.FoodItem
import com.example.zeroenqueue.FoodItemAdapter
import com.example.zeroenqueue.R
import com.example.zeroenqueue.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

private var _binding: FragmentHomeBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

    //inflater: LayoutInflater, container: ViewGroup?,
    //    savedInstanceState: Bundle?): View
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?): View? {
      //_binding = FragmentHomeBinding.inflate(inflater, container, false)
      //val root: View = binding.root

      //val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

      //val textView: TextView = binding.textHome
      //homeViewModel.text.observe(viewLifecycleOwner) {
        //  textView.text = it
      //}

    //return root
      return inflater.inflate(R.layout.fragment_home, container, false)
  }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val foodItems = listOf(
            FoodItem(1, "https://i1.lensdump.com/i/tJ76mZ.png", 78, 132123, "Fish and Chips",
            "The Tea Party", "Western", true),
            FoodItem(2, "https://i1.lensdump.com/i/tJ76mZ.png", 78, 132123, "Fish and Chips",
                "The Tea Party", "Western", false),
            FoodItem(3, "https://i1.lensdump.com/i/tJ76mZ.png", 78, 132123, "Fish and Chips",
                "The Tea Party", "Western", false)
        )
        recyclerViewHome.layoutManager = LinearLayoutManager(activity)
        recyclerViewHome.adapter = FoodItemAdapter(foodItems)
    }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

