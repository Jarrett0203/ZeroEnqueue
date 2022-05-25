package com.example.zeroenqueue.ui.foodList

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.FoodListAdapter
import com.example.zeroenqueue.classes.Food
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.FragmentFoodListBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dmax.dialog.SpotsDialog

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
    private lateinit var chipGroup : ChipGroup
    private lateinit var chipChicken : Chip
    private lateinit var chipChineseVegetarian: Chip
    private lateinit var chipEasternSoups : Chip
    private lateinit var chipFingerFoods: Chip
    private lateinit var chipFish : Chip
    private lateinit var chipMediFoods: Chip
    private lateinit var chipPasta : Chip
    private lateinit var chipPizza: Chip
    private lateinit var chipSandwiches : Chip
    private lateinit var chipSnacks: Chip
    private lateinit var chipWesternSoups : Chip


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        foodListViewModel =
            ViewModelProvider(this)[FoodListViewModel::class.java]

        _binding = FragmentFoodListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        recyclerViewFoodList = binding.recyclerFoodList

        chipGroup = binding.chipGroup
        chipChicken = binding.categoryChicken
        chipChineseVegetarian = binding.categoryChineseVegetarian
        chipEasternSoups = binding.categoryEasternSoups
        chipFingerFoods = binding.categoryFingerFoods
        chipFish = binding.categoryFish
        chipMediFoods = binding.categoryMedifoods
        chipPasta = binding.categoryPasta
        chipPizza = binding.categoryPizza
        chipSandwiches = binding.categorySandwiches
        chipSnacks = binding.categorySnacks
        chipWesternSoups = binding.categoryWesternSoups

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
        recyclerViewFoodList.setHasFixedSize(true)
        recyclerViewFoodList.layoutManager = LinearLayoutManager(context)
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)

        val selectedData : ArrayList<String> = arrayListOf()

        val checkedChangedListener = CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b){
                selectedData.add(compoundButton.text.toString())
                foodListViewModel.loadFoodList()
                foodListViewModel.loadFoodListWithCategory(selectedData)
            }
            else {
                selectedData.remove(compoundButton.text.toString())
                foodListViewModel.loadFoodList()
                if(selectedData.isNotEmpty()) {
                    foodListViewModel.loadFoodListWithCategory(selectedData)
                }
            }
        }
        chipChicken.setOnCheckedChangeListener(checkedChangedListener)
        chipChineseVegetarian.setOnCheckedChangeListener(checkedChangedListener)
        chipEasternSoups.setOnCheckedChangeListener(checkedChangedListener)
        chipFingerFoods.setOnCheckedChangeListener(checkedChangedListener)
        chipFish.setOnCheckedChangeListener(checkedChangedListener)
        chipMediFoods.setOnCheckedChangeListener(checkedChangedListener)
        chipPasta.setOnCheckedChangeListener(checkedChangedListener)
        chipPizza.setOnCheckedChangeListener(checkedChangedListener)
        chipSandwiches.setOnCheckedChangeListener(checkedChangedListener)
        chipSnacks.setOnCheckedChangeListener(checkedChangedListener)
        chipWesternSoups.setOnCheckedChangeListener(checkedChangedListener)
    }


    //search menu
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
                foodListViewModel.loadFoodList()
                binding.recyclerFoodList.scrollToPosition(0)
                foodListViewModel.loadFoodListSearch(s!!.lowercase())
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        if (adapter != null)
            adapter!!.onStop()
        super.onStop()
    }
}