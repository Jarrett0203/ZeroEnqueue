package com.example.zeroenqueue.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.contains
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.AddOnAdapter
import com.example.zeroenqueue.adapters.NewStallFoodListAdapter
import com.example.zeroenqueue.adapters.SizeAdapter
import com.example.zeroenqueue.classes.*
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.ActivityCreateNewStallBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
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
import java.util.*
import kotlin.collections.ArrayList
import com.google.android.material.textfield.TextInputEditText as TextInputEditText

class CreateNewStallActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateNewStallBinding
    private val foodStallRef = FirebaseDatabase.getInstance().reference.child(Common.FOODSTALL_REF)
    private val foodListRef = FirebaseDatabase.getInstance().reference.child(Common.FOODLIST_REF)
    private val storageRef = FirebaseStorage.getInstance().reference
    private lateinit var progressBar: ProgressBar
    private lateinit var editStallName: EditText
    private lateinit var editStallPhone: EditText
    private lateinit var editStallAddress: EditText
    private lateinit var editStallDescription: EditText
    private lateinit var newStallName: String
    private lateinit var newStallPhone: String
    private lateinit var newStallAddress: String
    private lateinit var newStallDescription: String
    private lateinit var dialog: AlertDialog
    private lateinit var addNewFoodBottomSheetDialog: BottomSheetDialog
    private lateinit var chipGroupCategory: ChipGroup

    @SuppressLint("InflateParams")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateNewStallBinding.inflate(layoutInflater)
        val root: View = binding.root

        var stallImageUri: Uri? = null
        val stallImage = binding.addStallImage
        val stallImageCardView = binding.materialCardViewStallImage
        val stallImagePrompt = binding.addCardImageText
        val addNewFoodItem = binding.addNewFoodItem
        editStallName = binding.inputStallName
        editStallPhone = binding.inputStallPhone
        editStallAddress = binding.inputStallAddress
        editStallDescription = binding.inputDescription
        val recyclerNewStallMenu = binding.recyclerTempMenu
        val emptyMenu = binding.emptyMenu
        val confirm = binding.BtnConfirm
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Stall Creation"

        progressBar = binding.createNewStallProgressBar
        progressBar.visibility = View.INVISIBLE

        recyclerNewStallMenu.layoutManager = LinearLayoutManager(this)
        recyclerNewStallMenu.setHasFixedSize(true)

        addNewFoodBottomSheetDialog = BottomSheetDialog(this, R.style.DialogStyle)
        val layoutAddNewFood = layoutInflater.inflate(R.layout.layout_add_new_food_item, null)
        val addNewFoodImage = layoutAddNewFood.findViewById<MaterialCardView>(R.id.addNewFoodCardView)
        val newFoodImage = layoutAddNewFood.findViewById<ImageView>(R.id.food_image)
        val foodImagePrompt = layoutAddNewFood.findViewById<TextView>(R.id.image_prompt)
        val newFoodName = layoutAddNewFood.findViewById<TextInputEditText>(R.id.input_food_name)
        val newFoodPrice = layoutAddNewFood.findViewById<TextInputEditText>(R.id.input_price)
        val newFoodDescription = layoutAddNewFood.findViewById<TextInputEditText>(R.id.input_food_description)
        val addSize = layoutAddNewFood.findViewById<ImageView>(R.id.add_size_image)
        val addAddOn = layoutAddNewFood.findViewById<ImageView>(R.id.add_addon_image)
        val recyclerSize = layoutAddNewFood.findViewById<RecyclerView>(R.id.recyclerSize)
        val recyclerAddOns = layoutAddNewFood.findViewById<RecyclerView>(R.id.recyclerAddOns)
        val confirmAddNewFoodItem = layoutAddNewFood.findViewById<Button>(R.id.btnAddNewFood)
        var foodImageUri: Uri? = null
        chipGroupCategory = layoutAddNewFood.findViewById(R.id.layout_chip_group_category)
        addNewFoodBottomSheetDialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { view ->
                val behaviour = BottomSheetBehavior.from(view)
                val layoutParams = view.layoutParams
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
                view.layoutParams = layoutParams
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        addNewFoodBottomSheetDialog.setContentView(layoutAddNewFood)

        recyclerSize.layoutManager = LinearLayoutManager(this)
        recyclerAddOns.layoutManager = LinearLayoutManager(this)
        recyclerSize.isNestedScrollingEnabled = false
        recyclerAddOns.isNestedScrollingEnabled = false

        val newFoodStallMenu: ArrayList<Food> = ArrayList()
        val addOnAdapter = AddOnAdapter(this, ArrayList())
        val sizeAdapter = SizeAdapter(this, ArrayList())

        val stallImageResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    stallImageUri = data!!.data
                    stallImage.setImageURI(stallImageUri)
                    stallImagePrompt.text = "Change image..."
                }
            }

        val foodImageResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    foodImageUri = data!!.data
                    newFoodImage.setImageURI(foodImageUri)
                    foodImagePrompt.text = "Change image..."
                }
            }

        stallImageCardView.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "image/*"
            stallImageResultLauncher.launch(galleryIntent)
        }

        addNewFoodImage.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "image/*"
            foodImageResultLauncher.launch(galleryIntent)
        }

        addNewFoodItem.setOnClickListener {
            addNewFoodBottomSheetDialog.show()
            showAllCategories()
        }

        addSize.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val itemView = layoutInflater.inflate(R.layout.layout_add_size, null)
            val sizeName = itemView.findViewById<EditText>(R.id.sizeName)
            val sizePrice = itemView.findViewById<EditText>(R.id.sizePrice)
            builder.setNegativeButton("CANCEL") {dialogInterface, _ -> dialogInterface.dismiss()}
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
            val builder = AlertDialog.Builder(this)
            val itemView = layoutInflater.inflate(R.layout.layout_add_addon, null)
            val addOnName = itemView.findViewById<EditText>(R.id.addOnName)
            val addOnPrice = itemView.findViewById<EditText>(R.id.addOnPrice)
            builder.setNegativeButton("CANCEL") {dialogInterface, _ -> dialogInterface.dismiss()}
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

        confirmAddNewFoodItem.setOnClickListener {
            dialog = SpotsDialog.Builder().setContext(this).setCancelable(false).build()
            dialog.show()
            var categoryName: String? = null
            for (i in 0 until chipGroupCategory.childCount) {
                val chip = chipGroupCategory.getChildAt(i) as Chip
                if (chip.isChecked)
                    categoryName = chip.text.toString()
            }
            if (foodImageUri == null)
                Toast.makeText(
                    this@CreateNewStallActivity,
                    "Please select an image for the new food.",
                    Toast.LENGTH_SHORT
                ).show()
            else if (newFoodName.text.toString().trim().isEmpty() || newFoodPrice.text.toString()
                    .trim().isEmpty()
                || newFoodDescription.text.toString().trim().isEmpty()
            )
                Toast.makeText(
                    this@CreateNewStallActivity,
                    "Empty fields are not allowed!!",
                    Toast.LENGTH_SHORT
                ).show()
            else if (categoryName == null) {
                Toast.makeText(
                    this@CreateNewStallActivity,
                    "Please select a category",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                //add food item to recycler view
                val newFoodNameText = newFoodName.text.toString().trim()
                val newFoodPriceDouble = newFoodPrice.text.toString().trim().toDouble()
                val newFoodDescriptionText = newFoodDescription.text.toString().trim()
                var newFoodImageUri: String?
                if (newFoodStallMenu.isEmpty())
                    emptyMenu.visibility = View.GONE
                val fileRef: StorageReference = storageRef.child(
                    System.currentTimeMillis().toString() + "." + getFileExtension(foodImageUri!!)
                )
                fileRef.putFile(foodImageUri!!).addOnSuccessListener {
                    fileRef.downloadUrl.addOnSuccessListener {
                        progressBar.visibility = View.INVISIBLE
                        newFoodImageUri = it.toString()
                        val newFood = Food()
                        newFood.image = newFoodImageUri
                        newFood.name = newFoodNameText
                        newFood.price = newFoodPriceDouble
                        newFood.description = newFoodDescriptionText
                        newFood.categories = categoryName
                        newFood.size = sizeAdapter.sizeList
                        newFood.addon = addOnAdapter.addOnList
                        newFoodStallMenu.add(newFood)
                        recyclerNewStallMenu.adapter =
                            NewStallFoodListAdapter(this, newFoodStallMenu)
                        Toast.makeText(
                            this@CreateNewStallActivity,
                            "Food added successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }.addOnFailureListener {
                        progressBar.visibility = View.INVISIBLE
                        Toast.makeText(
                            this@CreateNewStallActivity,
                            "Uploading image failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                newFoodImage.setImageURI(null)
                foodImageUri = null
                foodImagePrompt.text = "Click here to add image..."
                newFoodName.text!!.clear()
                newFoodPrice.text!!.clear()
                newFoodDescription.text!!.clear()
                chipGroupCategory.clearCheck()
                sizeAdapter.clearList()
                addOnAdapter.clearList()
                addNewFoodBottomSheetDialog.dismiss()
            }
            dialog.dismiss()
        }

        confirm.setOnClickListener {
            val firstNums = arrayOf('6', '8', '9')
            newStallName = editStallName.text.toString().trim()
            newStallPhone = editStallPhone.text.toString().trim()
            newStallAddress = editStallAddress.text.toString().trim()
            newStallDescription = editStallDescription.text.toString().trim()
            if (stallImageUri == null)
                Toast.makeText(
                    this@CreateNewStallActivity,
                    "Please select an image for the new food stall.",
                    Toast.LENGTH_SHORT
                ).show()
            else if (newStallName.isEmpty() || newStallPhone.isEmpty() || newStallAddress.isEmpty() || newStallDescription.isEmpty())
                Toast.makeText(this, "Empty fields are not allowed!!", Toast.LENGTH_SHORT).show()
            else if (!Arrays.stream(firstNums)
                    .anyMatch { t -> t == newStallPhone[0] } || newStallPhone.length != 8
            )
                Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show()
            else {
                val newStallId: String? = foodStallRef.push().key
                uploadNewStallToFirebase(stallImageUri!!, newStallId)
                uploadNewFoodMenuToFirebase(newFoodStallMenu, newStallId)
            }
        }

        setContentView(root)
    }

    private fun uploadNewFoodMenuToFirebase(foodList: ArrayList<Food>, newStallId: String?) {
        for (food in foodList) {
            val id: String? = foodListRef.push().key
            foodListRef.child(id!!).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(
                            this@CreateNewStallActivity,
                            food.name + " already exists!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        food.foodStall = newStallId
                        food.id = id
                        foodListRef.child(id)
                            .setValue(food)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        this@CreateNewStallActivity,
                                        food.name + " added successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@CreateNewStallActivity,
                        error.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
        Toast.makeText(this@CreateNewStallActivity, "Please swipe up to refresh", Toast.LENGTH_SHORT).show()
        goToStallOverview()
    }

    private fun uploadNewStallToFirebase(imageUri: Uri, newStallId: String?) {
        val fileRef: StorageReference = storageRef.child(
            System.currentTimeMillis().toString() + "." + getFileExtension(imageUri)
        )
        fileRef.putFile(imageUri).addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener {
                progressBar.visibility = View.INVISIBLE
                val stallImage = it.toString()
                foodStallRef.child(newStallId!!)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                Toast.makeText(
                                    this@CreateNewStallActivity,
                                    "Food stall already exists!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val newFoodStall = FoodStall()
                                newFoodStall.id = newStallId
                                newFoodStall.image = stallImage
                                newFoodStall.name = newStallName
                                newFoodStall.phone = newStallPhone
                                newFoodStall.address = newStallAddress
                                newFoodStall.description = newStallDescription
                                newFoodStall.ownerUid = Common.currentUser!!.uid
                                foodStallRef.child(newStallId)
                                    .setValue(newFoodStall)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                this@CreateNewStallActivity,
                                                "Stall creation success",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(
                                this@CreateNewStallActivity,
                                error.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    })
            }
        }.addOnProgressListener {
            progressBar.visibility = View.VISIBLE
        }.addOnFailureListener {
            progressBar.visibility = View.INVISIBLE
            Toast.makeText(
                this@CreateNewStallActivity,
                "Uploading image failed",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun goToStallOverview() {
        val intent = Intent(this, StallsOverviewActivity::class.java)
        startActivity(intent)
    }

    private fun getFileExtension(imageUri: Uri): Any? {
        val cr: ContentResolver = contentResolver
        val mime: MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(imageUri))
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
                                    this@CreateNewStallActivity,
                                    compoundButton.text.toString() + " selected",
                                    Toast.LENGTH_SHORT
                                ).show()
                                selectedDataCategory.add(compoundButton.text.toString())
                            } else {
                                Toast.makeText(
                                    this@CreateNewStallActivity,
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
                Toast.makeText(this@CreateNewStallActivity, "Failed to load categories", Toast.LENGTH_SHORT).show()
            }
        })
    }
}