package com.example.zeroenqueue.ui.foodList

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.FoodListAdapter
import com.example.zeroenqueue.classes.Food
import com.example.zeroenqueue.databinding.FragmentFoodListBinding
import dmax.dialog.SpotsDialog
import kotlin.collections.ArrayList

class FoodListFragment : Fragment() {

    private var _binding: FragmentFoodListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var recyclerViewFoodList: RecyclerView
    private lateinit var layoutAnimationController: LayoutAnimationController
    private lateinit var dialog: AlertDialog
    private var adapter: FoodListAdapter? = null
    private lateinit var foodListViewModel: FoodListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        foodListViewModel =
            ViewModelProvider(this).get(FoodListViewModel::class.java)

        _binding = FragmentFoodListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        recyclerViewFoodList = binding.recyclerFoodList
        initView()

        foodListViewModel.foodList.observe(viewLifecycleOwner) {
            dialog.dismiss()
            adapter = FoodListAdapter(requireContext(), it)
            recyclerViewFoodList.adapter = adapter
            recyclerViewFoodList.layoutAnimation = layoutAnimationController

        }
        return root
    }

    private fun initView() {

        setHasOptionsMenu(true)

        dialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()
        dialog.show()
        layoutAnimationController =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)
        recyclerViewFoodList.setHasFixedSize(true)
        recyclerViewFoodList.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_food, menu)

        val menuItem = menu.findItem(R.id.search_food)

        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menuItem.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String?): Boolean {
                binding.recyclerFoodList.scrollToPosition(0)
                startSearch(s!!.lowercase())
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(s: String?): Boolean {

                return false
            }

        })

        val closeButton =
            searchView.findViewById<View>(androidx.appcompat.R.id.search_close_btn) as ImageView
        closeButton.setOnClickListener {
            val ed =
                searchView.findViewById<View>(androidx.appcompat.R.id.search_src_text) as EditText
            ed.setText("")
            searchView.setQuery("", false)
            searchView.onActionViewCollapsed()
            menuItem.collapseActionView()
            foodListViewModel.loadFoodList()
        }
    }

    private fun startSearch(s: String) {
        val resultFood = ArrayList<Food>()
        for (i in 0 until adapter!!.getItemCount()) {
            val food = adapter!!.getFoodList()[i]
            if (food.name!!.lowercase().contains(s))
                resultFood.add(food)
        }
        foodListViewModel.getFoodList().value = resultFood

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}