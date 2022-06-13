package com.example.zeroenqueue.uiVendor.vendorFoodDetail

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.media.Image
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.text.toUpperCase
import androidx.core.view.contains
import com.example.zeroenqueue.R
import com.example.zeroenqueue.classes.Category
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.FragmentFoodDetailBinding
import com.example.zeroenqueue.databinding.FragmentOrdersBinding
import com.example.zeroenqueue.databinding.FragmentVendorFoodDetailBinding
import com.example.zeroenqueue.uiVendor.orders.OrdersViewModel
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import dmax.dialog.SpotsDialog
import java.util.*

class VendorFoodDetailFragment : Fragment() {

    private var _binding: FragmentVendorFoodDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var dialog: AlertDialog
    private val foodListRef = FirebaseDatabase.getInstance().reference.child(Common.FOODLIST_REF)
    private val storageRef = FirebaseStorage.getInstance().reference
    private lateinit var chipGroupCategory: ChipGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val vendorFoodDetailViewModel =
            ViewModelProvider(this)[VendorFoodDetailViewModel::class.java]

        _binding = FragmentVendorFoodDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val foodImageCardView : MaterialCardView = binding.addNewFoodCardView
        val editFoodImage: ImageView = binding.foodImage
        val editFoodImagePrompt: TextView = binding.imagePrompt
        val editName: EditText = binding.inputFoodName
        val editPrice: EditText = binding.inputPrice
        val editDescription: EditText = binding.inputFoodDescription
        val ratingBar: RatingBar = binding.ratingBar
        chipGroupCategory = binding.layoutChipGroupCategory
        val btnConfirmChanges: Button = binding.btnConfirmFood
        var foodImageUri: Uri? = null

        showAllCategories()

        val foodImageResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    foodImageUri = data!!.data
                    editFoodImage.setImageURI(foodImageUri)
                }
            }

        foodImageCardView.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "image/*"
            foodImageResultLauncher.launch(galleryIntent)
        }

        btnConfirmChanges.setOnClickListener {
            dialog = SpotsDialog.Builder().setContext(requireContext()).setCancelable(false).build()
            dialog.show()
            var categoryName: String? = null
            for (i in 0 until chipGroupCategory.childCount) {
                val chip = chipGroupCategory.getChildAt(i) as Chip
                if (Common.foodSelected!!.categories!!.uppercase() == chip.text.toString().uppercase())
                    chip.isChecked = true
                if (chip.isChecked)
                    categoryName = chip.text.toString()
            }
            if (foodImageUri == null)
                Toast.makeText(
                    requireContext(),
                    "Please select an image for the new food.",
                    Toast.LENGTH_SHORT
                ).show()
            else if (editName.text.toString().trim().isEmpty() || editPrice.text.toString()
                    .trim().isEmpty()
                || editDescription.text.toString().trim().isEmpty()
            )
                Toast.makeText(
                    requireContext(),
                    "Empty fields are not allowed!!",
                    Toast.LENGTH_SHORT
                ).show()
            else if (categoryName == null) {
                Toast.makeText(
                    requireContext(),
                    "Please select a category",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {

            }
        }

        vendorFoodDetailViewModel.foodDetail.observe(viewLifecycleOwner) {

        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showAllCategories() {
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
                                    requireContext(),
                                    compoundButton.text.toString() + " selected",
                                    Toast.LENGTH_SHORT
                                ).show()
                                selectedDataCategory.add(compoundButton.text.toString())
                            } else {
                                Toast.makeText(
                                    requireContext(),
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
                    if (!chipGroupCategory.contains(chip))
                        chipGroupCategory.addView(chip)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load categories", Toast.LENGTH_SHORT).show()
            }
        })
    }

}