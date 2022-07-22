package com.example.zeroenqueue.uiCustomer.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.CategoryAdapter
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.FragmentCategoriesBinding
import com.example.zeroenqueue.common.SpacesItemDecoration
import com.example.zeroenqueue.eventBus.MenuItemBack
import dmax.dialog.SpotsDialog
import org.greenrobot.eventbus.EventBus

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var adapter:CategoryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val categoriesViewModel =
            ViewModelProvider(this)[CategoriesViewModel::class.java]

        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerCategories = binding.recyclerCategories
        val dialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()
        dialog.show()
        val layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)
        recyclerCategories.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(context, 2)
        layoutManager.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if(adapter != null){
                    when(adapter!!.getItemViewType(position)) {
                        Common.DEFAULT_COLUMN_COUNT -> 1
                        Common.FULL_WIDTH_COLUMN -> 2
                        else -> -1
                    }
                }
                else
                    -1
            }
        }
        recyclerCategories.layoutManager = layoutManager
        recyclerCategories.addItemDecoration(SpacesItemDecoration(8))

        categoriesViewModel.errorMessage.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        categoriesViewModel.categoryList.observe(viewLifecycleOwner) {
            dialog.dismiss()
            adapter = CategoryAdapter(requireContext(), it)
            recyclerCategories.adapter = adapter
            recyclerCategories.layoutAnimation = layoutAnimationController
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        EventBus.getDefault().postSticky(MenuItemBack())
        super.onDestroy()
    }
}