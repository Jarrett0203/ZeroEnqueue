package com.example.zeroenqueue.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.NewStallFoodListAdapter
import com.example.zeroenqueue.classes.Food
import com.example.zeroenqueue.classes.FoodStall
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.ActivityCreateNewStallBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
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

    @SuppressLint("InflateParams")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateNewStallBinding.inflate(layoutInflater)
        val root: View = binding.root

        var stallImageUri: Uri? = null
        val stallImage = binding.addStallImage
        val stallImageCardView = binding.materialCardViewStallImage
        val stallImagePrompt = binding.addStallImageText
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
        val addNewFoodImage =
            layoutAddNewFood.findViewById<MaterialCardView>(R.id.addNewFoodCardView)
        val newFoodImage = layoutAddNewFood.findViewById<ImageView>(R.id.food_image)
        val foodImagePrompt = layoutAddNewFood.findViewById<TextView>(R.id.image_prompt)
        val newFoodName = layoutAddNewFood.findViewById<TextInputEditText>(R.id.input_food_name)
        val newFoodPrice = layoutAddNewFood.findViewById<TextInputEditText>(R.id.input_price)
        val newFoodDescription =
            layoutAddNewFood.findViewById<TextInputEditText>(R.id.input_food_description)
        val addSize = layoutAddNewFood.findViewById<ImageView>(R.id.add_size_image)
        val addAddOn = layoutAddNewFood.findViewById<ImageView>(R.id.add_addon_image)
        val confirmAddNewFoodItem = layoutAddNewFood.findViewById<Button>(R.id.btnAddNewFood)
        var foodImageUri: Uri? = null
        addNewFoodBottomSheetDialog.setContentView(layoutAddNewFood)

        val newFoodStallMenu: ArrayList<Food> = ArrayList()

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
        }

        addSize.setOnClickListener {
            //create dropdown menu somehow
            //save food size string and size price
        }

        addAddOn.setOnClickListener {
            //create dropdown menu somehow
            //save addOn string and addOn price
        }

        confirmAddNewFoodItem.setOnClickListener {
            dialog = SpotsDialog.Builder().setContext(this).setCancelable(false).build()
            dialog.show()
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
            else {
                //add food item to recycler view
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
                        newFood.name = newFoodName.text.toString().trim()
                        newFood.price = newFoodPrice.text.toString().trim().toDouble()
                        newFood.description = newFoodDescription.text.toString().trim()
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
                addNewFoodBottomSheetDialog.dismiss()
            }
            dialog.dismiss()
            foodImageUri = null
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
                uploadNewStallToFirebase(stallImageUri!!)
                uploadNewFoodMenuToFirebase(newFoodStallMenu)
            }
        }

        setContentView(root)
    }

    private fun uploadNewFoodMenuToFirebase(foodList: ArrayList<Food>) {
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
                        food.foodStall = newStallName
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
        goToStallOverview()
    }

    private fun uploadNewStallToFirebase(imageUri: Uri) {
        val fileRef: StorageReference = storageRef.child(
            System.currentTimeMillis().toString() + "." + getFileExtension(imageUri)
        )
        fileRef.putFile(imageUri).addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener {
                progressBar.visibility = View.INVISIBLE
                val stallImage = it.toString()
                val id: String? = foodStallRef.push().key
                foodStallRef.child(id!!)
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
                                newFoodStall.id = id
                                newFoodStall.image = stallImage
                                newFoodStall.name = newStallName
                                newFoodStall.phone = newStallPhone
                                newFoodStall.address = newStallAddress
                                newFoodStall.description = newStallDescription
                                newFoodStall.ownerName = Common.currentUser!!.name
                                foodStallRef.child(id)
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
        val intent = Intent(this, VendorFoodStallsActivity::class.java)
        startActivity(intent)
    }

    private fun getFileExtension(imageUri: Uri): Any? {
        val cr: ContentResolver = contentResolver
        val mime: MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(imageUri))
    }
}