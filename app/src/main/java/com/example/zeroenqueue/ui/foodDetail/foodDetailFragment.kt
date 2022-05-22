package com.example.zeroenqueue.ui.foodDetail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.andremion.counterfab.CounterFab
import com.bumptech.glide.Glide
import com.example.zeroenqueue.R
import com.example.zeroenqueue.databinding.FragmentFoodDetailBinding
import com.example.zeroenqueue.databinding.FragmentFoodStallBinding
import com.example.zeroenqueue.model.Food
import com.example.zeroenqueue.ui.foodStall.FoodStallViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_food_detail.*

class foodDetailFragment : Fragment() {

    private var _binding: FragmentFoodDetailBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var food_image: ImageView
    private lateinit var btnCart: CounterFab
    private lateinit var btnRating: FloatingActionButton
    private lateinit var food_name: TextView
    private lateinit var food_description: TextView
    private lateinit var food_price: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var btnShowComments: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val foodDetailViewModel =
            ViewModelProvider(this).get(FoodDetailViewModel::class.java)

        _binding = FragmentFoodDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        food_image = binding.foodImage
        btnCart = binding.btnCart
        btnRating = binding.btnRating
        food_name = binding.foodName
        food_description = binding.foodDescription
        food_price = binding.foodPrice
        ratingBar = binding.ratingBar
        btnShowComments = binding.btnShowComments

        foodDetailViewModel.foodDetail.observe(viewLifecycleOwner) {
            displayInfo(it)
        }
        return root
    }

    private fun displayInfo(it: Food?) {
        context?.let { it1 -> Glide.with(it1).load(it!!.image).into(food_image) }
        food_name.text = StringBuilder(it!!.name!!)
        food_description.text = StringBuilder(it.description!!)
        food_price.text = StringBuilder(it.price.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}