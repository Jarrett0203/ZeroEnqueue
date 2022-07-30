package com.example.zeroenqueue.uiVendor.vendorFoodDetail

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.contains
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.AddOnAdapter
import com.example.zeroenqueue.adapters.SizeAdapter
import com.example.zeroenqueue.classes.AddOn
import com.example.zeroenqueue.classes.Category
import com.example.zeroenqueue.classes.Food
import com.example.zeroenqueue.classes.Size
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.FragmentVendorFoodDetailBinding
import com.example.zeroenqueue.uiCustomer.comment.CommentFragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dmax.dialog.SpotsDialog

class VendorFoodDetailFragment : Fragment() {

    private var _binding: FragmentVendorFoodDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var dialog: AlertDialog
    private val foodListRef = FirebaseDatabase.getInstance().reference.child(Common.FOODLIST_REF)
    private val storageRef = FirebaseStorage.getInstance().reference
    private lateinit var chipGroupCategory: ChipGroup
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val vendorFoodDetailViewModel =
            ViewModelProvider(this)[VendorFoodDetailViewModel::class.java]

        _binding = FragmentVendorFoodDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val foodImageCardView: MaterialCardView = binding.addNewFoodCardView
        val editFoodImage: ImageView = binding.foodImage
        val editFoodImagePrompt: TextView = binding.imagePrompt
        val editName: EditText = binding.inputFoodName
        val editPrice: EditText = binding.inputPrice
        val editDescription: EditText = binding.inputFoodDescription
        val ratingBar: RatingBar = binding.ratingBar
        val addSize: ImageView = binding.addSizeImage
        val addAddOn: ImageView = binding.addAddonImage
        val recyclerSize: RecyclerView = binding.recyclerSize
        val recyclerAddOns: RecyclerView = binding.recyclerAddOns
        chipGroupCategory = binding.layoutChipGroupCategory
        val btnShowComments = binding.btnShowComments
        val btnConfirmChanges: Button = binding.btnConfirmFood
        var foodImageUri: Uri? = null
        navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment_content_main_vendor)

        recyclerSize.layoutManager = LinearLayoutManager(context)
        recyclerAddOns.layoutManager = LinearLayoutManager(context)

        var addOnAdapter = AddOnAdapter(requireContext(), ArrayList())
        var sizeAdapter = SizeAdapter(requireContext(), ArrayList())

        showAllCategories()

        if (Common.foodSelected == null) {
            editFoodImagePrompt.text = "Add new food image..."
            ratingBar.visibility = View.GONE
            btnShowComments.visibility = View.GONE
            (activity as AppCompatActivity).supportActionBar?.title = "Add Food Item"
        } else {
            (activity as AppCompatActivity).supportActionBar?.title = "Edit Food Detail"
        }

        addSize.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val itemView = layoutInflater.inflate(R.layout.layout_add_size, null)
            val sizeName = itemView.findViewById<EditText>(R.id.sizeName)
            val sizePrice = itemView.findViewById<EditText>(R.id.sizePrice)
            builder.setNegativeButton("CANCEL") { dialogInterface, _ -> dialogInterface.dismiss() }
            builder.setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
                val newSize = Size()
                newSize.name = sizeName.text.toString()
                newSize.price = sizePrice.text.toString().toDouble()
                sizeAdapter.sizeList.add(0, newSize)
                sizeAdapter.notifyItemInserted(0)
                recyclerSize.adapter = sizeAdapter
            }

            builder.setView(itemView)
            val uploadDialog = builder.create()
            uploadDialog.show()
        }

        addAddOn.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val itemView = layoutInflater.inflate(R.layout.layout_add_addon, null)
            val addOnName = itemView.findViewById<EditText>(R.id.addOnName)
            val addOnPrice = itemView.findViewById<EditText>(R.id.addOnPrice)
            builder.setNegativeButton("CANCEL") { dialogInterface, _ -> dialogInterface.dismiss() }
            builder.setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
                val newAddOn = AddOn()
                newAddOn.name = addOnName.text.toString()
                newAddOn.price = addOnPrice.text.toString().toDouble()
                addOnAdapter.addOnList.add(0, newAddOn)
                addOnAdapter.notifyItemInserted(0)
                recyclerAddOns.adapter = addOnAdapter
            }

            builder.setView(itemView)
            val uploadDialog = builder.create()
            uploadDialog.show()
        }

        btnShowComments.setOnClickListener {
            val commentFragment = CommentFragment.getInstance()
            commentFragment.show(requireActivity().supportFragmentManager, "CommentFragment")
        }

        btnConfirmChanges.setOnClickListener {
            dialog = SpotsDialog.Builder().setContext(requireContext()).setCancelable(false).build()
            var confirmedCategoryName: String? = null
            for (i in 0 until chipGroupCategory.childCount) {
                val chip = chipGroupCategory.getChildAt(i) as Chip
                if (chip.isChecked)
                    confirmedCategoryName = chip.text.toString()
            }
            if (editName.text.toString().trim().isEmpty() || editPrice.text.toString()
                    .trim().isEmpty()
                || editDescription.text.toString().trim().isEmpty()
            )
                Toast.makeText(
                    requireContext(),
                    "Empty fields are not allowed!!",
                    Toast.LENGTH_SHORT
                ).show()
            else if (confirmedCategoryName == null) {
                Toast.makeText(
                    requireContext(),
                    "Please select a category",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                val editFoodNameText = editName.text.toString().trim()
                val editFoodPriceDouble = editPrice.text.toString().trim().toDouble()
                val editFoodDescriptionText = editDescription.text.toString().trim()
                var editFoodImageUri: String?
                val editFood = Food()
                if (foodImageUri == null) {
                    if (Common.foodSelected == null) {
                        Toast.makeText(
                            requireContext(),
                            "Please select an image for the new food.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        editFood.id = Common.foodSelected!!.id
                        editFood.image = Common.foodSelected!!.image
                        editFood.name = editFoodNameText
                        editFood.price = editFoodPriceDouble
                        editFood.description = editFoodDescriptionText
                        editFood.categories = confirmedCategoryName
                        editFood.size = sizeAdapter.sizeList
                        editFood.addon = addOnAdapter.addOnList
                        uploadFoodToFirebase(editFood)
                    }
                } else {
                    val fileRef: StorageReference = storageRef.child(
                        System.currentTimeMillis()
                            .toString() + "." + getFileExtension(foodImageUri!!)
                    )
                    fileRef.putFile(foodImageUri!!).addOnSuccessListener {
                        fileRef.downloadUrl.addOnSuccessListener {
                            editFoodImageUri = it.toString()
                            if (Common.foodSelected != null)
                                editFood.id = Common.foodSelected!!.id
                            else
                                editFood.id = foodListRef.push().key
                            editFood.image = editFoodImageUri
                            editFood.name = editFoodNameText
                            editFood.price = editFoodPriceDouble
                            editFood.description = editFoodDescriptionText
                            editFood.categories = confirmedCategoryName
                            editFood.size = sizeAdapter.sizeList
                            editFood.addon = addOnAdapter.addOnList
                            Toast.makeText(
                                requireContext(),
                                "Food image uploaded successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            uploadFoodToFirebase(editFood)
                        }.addOnFailureListener {
                            Toast.makeText(
                                requireContext(),
                                "Uploading image failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        vendorFoodDetailViewModel.foodDetail.observe(viewLifecycleOwner) {
            if (Common.foodSelected != null) {
                context?.let { i -> Glide.with(i).load(it!!.image).into(editFoodImage) }
                editName.setText(it!!.name!!)
                editDescription.setText(it.description!!)
                editPrice.setText(Common.formatPrice(it.price))
                ratingBar.rating = (it.ratingValue / it.ratingCount).toFloat()
                addOnAdapter = AddOnAdapter(requireContext(), it.addon)
                sizeAdapter = SizeAdapter(requireContext(), it.size)
                recyclerAddOns.adapter = addOnAdapter
                recyclerSize.adapter = sizeAdapter
            }
        }
        val foodImageResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    foodImageUri = data!!.data
                    editFoodImage.setImageURI(foodImageUri)
                    editFoodImagePrompt.text = "Change image..."
                }
            }

        foodImageCardView.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "image/*"
            foodImageResultLauncher.launch(galleryIntent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Common.foodSelected = null
    }

    private fun getFileExtension(imageUri: Uri): Any? {
        val cr: ContentResolver = activity!!.contentResolver
        val mime: MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(imageUri))
    }

    private fun uploadFoodToFirebase(food: Food) {
        val id: String? = food.id
        foodListRef.child(id!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val updateData = HashMap<String, Any>()
                    updateData["image"] = food.image!!
                    updateData["name"] = food.name!!
                    updateData["price"] = food.price
                    updateData["description"] = food.description!!
                    updateData["categories"] = food.categories!!
                    updateData["size"] = food.size
                    updateData["addon"] = food.addon
                    snapshot.ref
                        .updateChildren(updateData)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                dialog.show()
                                Toast.makeText(
                                    requireContext(),
                                    "Updated " + food.name,
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate(R.id.navigation_stall_menu)
                                dialog.dismiss()
                            }
                        }
                } else {
                    dialog.show()
                    food.foodStall = Common.foodStallSelected!!.id
                    foodListRef.child(id)
                        .setValue(food)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    food.name + " added successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate(R.id.navigation_stall_menu)
                                dialog.dismiss()
                            }
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
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
                                selectedDataCategory.remove(compoundButton.text.toString())
                            }
                        }
                    chip.setOnCheckedChangeListener(checkedChangedListenerCategory)
                    if (Common.foodSelected != null) {
                        if (Common.foodSelected!!.categories!!.uppercase() == chip.text.toString()
                                .uppercase()
                        ) {
                            chip.isChecked = true
                        }
                    }
                    if (!chipGroupCategory.contains(chip))
                        chipGroupCategory.addView(chip)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load categories", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

}