package com.example.zeroenqueue.ui.foodList

import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.graphics.Rect
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
import androidx.window.layout.WindowMetricsCalculator
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.FoodListAdapter
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
    private lateinit var chipGroupCategory: ChipGroup
    private lateinit var chipGroupFoodStall: ChipGroup
    private lateinit var chipChicken: Chip
    private lateinit var chipChineseVegetarian: Chip
    private lateinit var chipSnacks: Chip
    private lateinit var chipWesternSoups: Chip
    private lateinit var chipChickenStall: Chip
    private lateinit var chipVegetarianStall: Chip
    private lateinit var chipMedifoods: Chip
    private lateinit var chipWesternStall: Chip

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

        chipGroupCategory = binding.chipGroupCategory
        chipGroupFoodStall = binding.chipGroupFoodStall
        chipChicken = binding.categoryChicken
        chipChineseVegetarian = binding.categoryChineseVegetarian
        chipSnacks = binding.categorySnacks
        chipWesternSoups = binding.categoryWesternSoups
        chipChickenStall = binding.chickenStall
        chipVegetarianStall = binding.vegetarianStall
        chipMedifoods = binding.medifoodsStall
        chipWesternStall = binding.westernStall

        initView()

        if (Common.foodStallSelected != null) {
            foodListViewModel.foodListWithFoodStall.observe(viewLifecycleOwner) {
                dialog.dismiss()
                adapter = FoodListAdapter(requireContext(), it)
                recyclerViewFoodList.adapter = adapter
                recyclerViewFoodList.layoutAnimation = layoutAnimationController
            }
        }
        else if (Common.categorySelected != null) {
            foodListViewModel.foodListWithCategory.observe(viewLifecycleOwner) {
                dialog.dismiss()
                adapter = FoodListAdapter(requireContext(), it)
                recyclerViewFoodList.adapter = adapter
                recyclerViewFoodList.layoutAnimation = layoutAnimationController
            }
        }
        else {
            foodListViewModel.foodList.observe(viewLifecycleOwner) {
                dialog.dismiss()
                adapter = FoodListAdapter(requireContext(), it)
                recyclerViewFoodList.adapter = adapter
                recyclerViewFoodList.layoutAnimation = layoutAnimationController
            }
        }
        return root
    }

    private fun initView() {
        setHasOptionsMenu(true)
        dialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()
        dialog.show()
        recyclerViewFoodList.setHasFixedSize(true)
        recyclerViewFoodList.layoutManager = LinearLayoutManager(context)
        layoutAnimationController =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)

        filterWithFoodStallOrCategory()

    }

    private fun filterWithFoodStallOrCategory() {
        val windowMetrics =
            WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(requireActivity())
        val currentBounds = windowMetrics.bounds
        val widthScreen = currentBounds.width()

        val scrollViewCategory = binding.horizontalScrollViewCategory
        val scrollViewFoodStall = binding.horizontalScrollViewFoodStall

        val selectedDataCategory: ArrayList<String> = arrayListOf()
        val selectedDataFoodStall: ArrayList<String> = arrayListOf()

        val checkedChangedListenerCategory = CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b) {
                Toast.makeText(
                    context,
                    compoundButton.text.toString() + " selected",
                    Toast.LENGTH_SHORT
                ).show()
                selectedDataCategory.add(compoundButton.text.toString())
                binding.recyclerFoodList.scrollToPosition(0)
                foodListViewModel.loadFoodListWithCategory(selectedDataCategory)
            } else {
                Toast.makeText(
                    context,
                    compoundButton.text.toString() + " unselected",
                    Toast.LENGTH_SHORT
                ).show()
                selectedDataCategory.remove(compoundButton.text.toString())
                binding.recyclerFoodList.scrollToPosition(0)
                foodListViewModel.loadFoodList()
                if (selectedDataCategory.isNotEmpty()) {
                    foodListViewModel.loadFoodListWithCategory(selectedDataCategory)
                }
            }
        }
        val checkedChangedListenerFoodStall = CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b) {
                Toast.makeText(
                    context,
                    compoundButton.text.toString() + " selected",
                    Toast.LENGTH_SHORT
                ).show()
                selectedDataFoodStall.add(compoundButton.text.toString())
                binding.recyclerFoodList.scrollToPosition(0)
                foodListViewModel.loadFoodListWithFoodStall(selectedDataFoodStall)
            } else {
                Toast.makeText(
                    context,
                    compoundButton.text.toString() + " unselected",
                    Toast.LENGTH_SHORT
                ).show()
                selectedDataFoodStall.remove(compoundButton.text.toString())
                binding.recyclerFoodList.scrollToPosition(0)
                foodListViewModel.loadFoodList()
                if (selectedDataFoodStall.isNotEmpty()) {
                    foodListViewModel.loadFoodListWithFoodStall(selectedDataFoodStall)
                }
            }
        }

        for (i in 0 until chipGroupCategory.childCount) {
            val chip = chipGroupCategory.getChildAt(i) as Chip
            chip.setOnCheckedChangeListener(checkedChangedListenerCategory)
            chip.setOnClickListener {
                val r = Rect()
                it.getGlobalVisibleRect(r)

                if (chipChicken.isChecked) {
                    val rr = Rect()
                    it.getDrawingRect(rr)
                    scrollViewCategory.smoothScrollBy(rr.right - (widthScreen - r.right), 0)
                }

                if (chipChineseVegetarian.isChecked) {
                    val rr = Rect()
                    it.getDrawingRect(rr)
                    scrollViewCategory.smoothScrollBy(rr.right - (widthScreen - r.right), 0)
                }

                if (chipSnacks.isChecked) {
                    val rr = Rect()
                    it.getDrawingRect(rr)
                    scrollViewCategory.smoothScrollBy(r.right, 0)
                }

                if (chipWesternSoups.isChecked) {
                    val rr = Rect()
                    it.getDrawingRect(rr)
                    scrollViewCategory.smoothScrollBy(r.right, 0)
                }
                else {
                    scrollViewCategory.smoothScrollTo(
                        chip.left - (widthScreen / 2) + (chip.width / 2),
                        0
                    )
                }
            }
            if (Common.categorySelected != null) {
                if (chip.text.toString().uppercase() == Common.categorySelected!!.name!!) {
                    chip.isChecked = true
                }
            }
        }
        for (i in 0 until chipGroupFoodStall.childCount) {
            val chip = chipGroupFoodStall.getChildAt(i) as Chip
            chip.setOnCheckedChangeListener(checkedChangedListenerFoodStall)
            chip.setOnClickListener {
                val r = Rect()
                it.getGlobalVisibleRect(r)

                if (chipChickenStall.isChecked) {
                    val rr = Rect()
                    it.getDrawingRect(rr)
                    scrollViewFoodStall.smoothScrollBy(rr.right - (widthScreen - r.right), 0)
                }

                if (chipVegetarianStall.isChecked) {
                    val rr = Rect()
                    it.getDrawingRect(rr)
                    scrollViewFoodStall.smoothScrollBy(rr.right - (widthScreen - r.right), 0)
                }

                if (chipMedifoods.isChecked) {
                    val rr = Rect()
                    it.getDrawingRect(rr)
                    scrollViewFoodStall.smoothScrollBy(r.right, 0)
                }

                if (chipWesternStall.isChecked) {
                    val rr = Rect()
                    it.getDrawingRect(rr)
                    scrollViewFoodStall.smoothScrollBy(r.right, 0)
                }
                else {
                    scrollViewFoodStall.smoothScrollTo(
                        chip.left - (widthScreen / 2) + (chip.width / 2),
                        0
                    )
                }
            }
            if (Common.foodStallSelected != null) {
                if (chip.text.toString().uppercase() == Common.foodStallSelected!!.name!!.uppercase()) {
                    chip.isChecked = true
                }
            }
        }
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