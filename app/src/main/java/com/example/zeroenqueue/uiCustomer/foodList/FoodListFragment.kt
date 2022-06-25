package com.example.zeroenqueue.uiCustomer.foodList

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.FoodListAdapter
import com.example.zeroenqueue.classes.Category
import com.example.zeroenqueue.classes.FoodStall
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.FragmentFoodListBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
    private lateinit var fabFilter: FloatingActionButton
    private lateinit var btnFilter: Button

    private lateinit var filterBottomSheetDialog: BottomSheetDialog

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
        fabFilter = binding.filterFab

        initView()

        if (Common.foodStallSelected != null) {
            foodListViewModel.foodListWithFoodStall.observe(viewLifecycleOwner) {
                dialog.dismiss()
                adapter = FoodListAdapter(requireContext(), it)
                recyclerViewFoodList.adapter = adapter
                recyclerViewFoodList.layoutAnimation = layoutAnimationController
            }
        } else if (Common.categorySelected != null) {
            foodListViewModel.foodListWithCategory.observe(viewLifecycleOwner) {
                dialog.dismiss()
                adapter = FoodListAdapter(requireContext(), it)
                recyclerViewFoodList.adapter = adapter
                recyclerViewFoodList.layoutAnimation = layoutAnimationController
            }
        } else {
            foodListViewModel.foodList.observe(viewLifecycleOwner) {
                dialog.dismiss()
                adapter = FoodListAdapter(requireContext(), it)
                recyclerViewFoodList.adapter = adapter
                recyclerViewFoodList.layoutAnimation = layoutAnimationController
            }
        }
        return root
    }

    @SuppressLint("InflateParams")
    private fun initView() {

        filterBottomSheetDialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
        val layout_filter = layoutInflater.inflate(R.layout.layout_filter_food, null)
        chipGroupCategory = layout_filter.findViewById(R.id.layout_chip_group_category) as ChipGroup
        chipGroupFoodStall =
            layout_filter.findViewById(R.id.layout_chip_group_food_stall) as ChipGroup
        btnFilter = layout_filter.findViewById(R.id.btnFilter) as Button
        filterBottomSheetDialog.setContentView(layout_filter)

        setHasOptionsMenu(true)
        dialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()
        dialog.show()
        recyclerViewFoodList.setHasFixedSize(true)
        recyclerViewFoodList.layoutManager = LinearLayoutManager(context)
        layoutAnimationController =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)

        val selectedDataCategory = showAllCategories()
        val selectedDataFoodStall = showAllFoodStalls()

        btnFilter.setOnClickListener {
            dialog.show()
            filter(selectedDataFoodStall, selectedDataCategory)
            dialog.dismiss()
            filterBottomSheetDialog.dismiss()
        }

        fabFilter.setOnClickListener {
            filterBottomSheetDialog.show()
        }

    }

    private fun filter(
        selectedDataFoodStall: ArrayList<FoodStall>,
        selectedDataCategory: ArrayList<String>
    ) {
        binding.recyclerFoodList.scrollToPosition(0)
        foodListViewModel.loadFoodList()
        if (selectedDataCategory.isEmpty() && selectedDataFoodStall.isEmpty()) {
            foodListViewModel.loadFoodList()
        } else if (selectedDataFoodStall.isEmpty()) {
            foodListViewModel.loadFoodListWithCategory(selectedDataCategory)
        } else if (selectedDataCategory.isEmpty()) {
            foodListViewModel.loadFoodListWithFoodStall(selectedDataFoodStall)
        } else {
            foodListViewModel.loadFoodListWithFoodStallAndCategories(
                selectedDataFoodStall,
                selectedDataCategory
            )
        }
    }

    private fun showAllFoodStalls(): ArrayList<FoodStall> {
        val selectedDataFoodStall: ArrayList<FoodStall> = arrayListOf()
        val stallRef =
            FirebaseDatabase.getInstance(Common.DATABASE_LINK).getReference(Common.FOODSTALL_REF)
        stallRef.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("InflateParams")
            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemSnapShot in snapshot.children) {
                    val foodStall = itemSnapShot.getValue(FoodStall::class.java)
                    val chip =
                        layoutInflater.inflate(R.layout.layout_chip_filter, null, false) as Chip
                    chip.text = foodStall!!.name
                    val checkedChangedListenerFoodStall =
                        CompoundButton.OnCheckedChangeListener { compoundButton, b ->
                            if (b) {
                                Toast.makeText(
                                    context,
                                    compoundButton.text.toString() + " selected",
                                    Toast.LENGTH_SHORT
                                ).show()
                                selectedDataFoodStall.add(foodStall)
                            } else {
                                Toast.makeText(
                                    context,
                                    compoundButton.text.toString() + " unselected",
                                    Toast.LENGTH_SHORT
                                ).show()
                                selectedDataFoodStall.remove(foodStall)
                            }
                        }
                    chip.setOnCheckedChangeListener(checkedChangedListenerFoodStall)
                    if (Common.foodStallSelected != null) {
                        if (chip.text.toString()
                                .uppercase() == Common.foodStallSelected!!.name!!.uppercase()
                        ) {
                            chip.isChecked = true
                        }
                    }
                    chipGroupFoodStall.addView(chip)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load food stalls", Toast.LENGTH_SHORT).show()
            }
        })
        return selectedDataFoodStall
    }

    private fun showAllCategories(): ArrayList<String> {
        val selectedDataCategory: ArrayList<String> = arrayListOf()
        val categoryRef =
            FirebaseDatabase.getInstance(Common.DATABASE_LINK).getReference(Common.CATEGORY_REF)
        categoryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("InflateParams")
            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemSnapShot in snapshot.children) {
                    val category = itemSnapShot.getValue(Category::class.java)
                    val chip =
                        layoutInflater.inflate(R.layout.layout_chip_filter, null, false) as Chip
                    chip.text = category!!.name
                    val checkedChangedListenerCategory =
                        CompoundButton.OnCheckedChangeListener { compoundButton, b ->
                            if (b) {
                                Toast.makeText(
                                    context,
                                    compoundButton.text.toString() + " selected",
                                    Toast.LENGTH_SHORT
                                ).show()
                                selectedDataCategory.add(compoundButton.text.toString())
                            } else {
                                Toast.makeText(
                                    context,
                                    compoundButton.text.toString() + " unselected",
                                    Toast.LENGTH_SHORT
                                ).show()
                                selectedDataCategory.remove(compoundButton.text.toString())
                            }
                        }
                    chip.setOnCheckedChangeListener(checkedChangedListenerCategory)
                    if (Common.categorySelected != null) {
                        if (chip.text.toString().uppercase() == Common.categorySelected!!.name!!) {
                            chip.isChecked = true
                        }
                    }
                    chipGroupCategory.addView(chip)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load categories", Toast.LENGTH_SHORT).show()
            }
        })
        return selectedDataCategory
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

    //old filter
    /*private fun filterWithFoodStallOrCategory() {
        val windowMetrics =
            WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(requireActivity())
        val currentBounds = windowMetrics.bounds
        val widthScreen = currentBounds.width()

        val scrollViewCategory = binding.horizontalScrollViewCategory
        val scrollViewFoodStall = binding.horizontalScrollViewFoodStall

        val selectedDataCategory: ArrayList<String> = arrayListOf()
        val selectedDataFoodStall: ArrayList<String> = arrayListOf()

        val checkedChangedListenerCategory =
            CompoundButton.OnCheckedChangeListener { compoundButton, b ->
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
        val checkedChangedListenerFoodStall =
            CompoundButton.OnCheckedChangeListener { compoundButton, b ->
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
                } else {
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
                } else {
                    scrollViewFoodStall.smoothScrollTo(
                        chip.left - (widthScreen / 2) + (chip.width / 2),
                        0
                    )
                }
            }
            if (Common.foodStallSelected != null) {
                if (chip.text.toString()
                        .uppercase() == Common.foodStallSelected!!.name!!.uppercase()
                ) {
                    chip.isChecked = true
                }
            }
        }
    }*/
}