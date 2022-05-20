package com.example.zeroenqueue.ui.categories

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.CategoryAdapter
import com.example.zeroenqueue.databinding.FragmentCategoriesBinding
import com.example.zeroenqueue.common.SpacesItemDecoration
import dmax.dialog.SpotsDialog

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var dialog: AlertDialog
    private lateinit var layoutAnimationController: LayoutAnimationController
    private var adapter:CategoryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val categoriesViewModel =
            ViewModelProvider(this).get(CategoriesViewModel::class.java)

        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = binding.recyclerCategories
        initView()

        categoriesViewModel.errorMessage.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }

        categoriesViewModel.categoryList.observe(viewLifecycleOwner) {
            dialog.dismiss()
            adapter = CategoryAdapter(requireContext(), it)
            recyclerView.adapter = adapter
            recyclerView.layoutAnimation = layoutAnimationController
        }

        return root
    }

    private fun initView() {
        dialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()
        dialog.show()
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)
        recyclerView.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(context, 2)
        layoutManager.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if(adapter != null){
                    when(adapter!!.getItemViewType(position)) {
                        0 -> 1
                        1 -> 2
                        else -> -1
                    }
                }else
                    -1
            }
        }
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(SpacesItemDecoration(8))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}