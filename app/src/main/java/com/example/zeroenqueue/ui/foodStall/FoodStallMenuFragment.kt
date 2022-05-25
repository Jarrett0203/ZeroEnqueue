package com.example.zeroenqueue.ui.foodStall

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import android.view.animation.LayoutAnimationController
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.FoodStallMenuAdapter
import com.example.zeroenqueue.classes.Food
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.FragmentFoodStallMenuBinding
import dmax.dialog.SpotsDialog

class FoodStallMenuFragment : Fragment() {

    private var _binding: FragmentFoodStallMenuBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var recyclerViewFoodList: RecyclerView
    private lateinit var layoutAnimationController: LayoutAnimationController
    private lateinit var dialog: AlertDialog
    private var adapter : FoodStallMenuAdapter?=null
    private lateinit var foodStallMenuViewModel: FoodStallMenuViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        foodStallMenuViewModel =
            ViewModelProvider(this)[FoodStallMenuViewModel::class.java]

        _binding = FragmentFoodStallMenuBinding.inflate(inflater, container, false)
        val root: View = binding.root
        recyclerViewFoodList = binding.recyclerFoodList
        initView()

        foodStallMenuViewModel.foodList.observe(viewLifecycleOwner) {
            dialog.dismiss()
            adapter = FoodStallMenuAdapter(requireContext(), it)
            recyclerViewFoodList.adapter = adapter
            recyclerViewFoodList.layoutAnimation = layoutAnimationController
        }
        return root
    }

    private fun initView() {
        setHasOptionsMenu(true)
        dialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()
        dialog.show()
        recyclerViewFoodList.setHasFixedSize(true)
        recyclerViewFoodList.layoutManager = LinearLayoutManager(context)
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)
        (activity as AppCompatActivity).supportActionBar!!.title = Common.foodStallSelected!!.name
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
            foodStallMenuViewModel.loadFoodStallMenu()
        }
    }

    private fun startSearch(s: String) {
        val resultFood = ArrayList<Food>()
        for (i in 0 until adapter!!.getItemCount()) {
            val food = adapter!!.getFoodStallMenu()[i]
            if (food.name!!.lowercase().contains(s))
                resultFood.add(food)
        }
        foodStallMenuViewModel.getCategoryFoodList().value = resultFood

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
