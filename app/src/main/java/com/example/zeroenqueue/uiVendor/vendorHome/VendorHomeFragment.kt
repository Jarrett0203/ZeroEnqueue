package com.example.zeroenqueue.uiVendor.vendorHome

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.VendorFoodListAdapter
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.FragmentVendorHomeBinding
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dmax.dialog.SpotsDialog
import java.util.*
import kotlin.collections.HashMap

class VendorHomeFragment : Fragment() {

    private var _binding: FragmentVendorHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var dialog: AlertDialog
    private val storageRef = FirebaseStorage.getInstance().reference
    private val foodStallRef = FirebaseDatabase.getInstance().getReference(Common.FOODSTALL_REF)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val vendorHomeViewModel =
            ViewModelProvider(this)[VendorHomeViewModel::class.java]

        _binding = FragmentVendorHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val swipeRefreshLayout = binding.swipeRefresh
        val recyclerViewPopularFood = binding.recyclerPopular
        val editStallDetails = binding.editStallDetails
        val filterPopularFood = binding.filterPopularFood

        val stallName = binding.stallName
        val stallPhone = binding.stallPhone
        val stallAddress = binding.stallAddress
        val stallDescription = binding.stallDescription
        val stallImage = binding.stallImage

        stallName.text = Common.foodStallSelected!!.name
        stallPhone.text = Common.foodStallSelected!!.phone
        stallAddress.text = Common.foodStallSelected!!.address
        stallDescription.text = Common.foodStallSelected!!.description
        Glide.with(activity!!).load(Common.foodStallSelected!!.image).into(stallImage)

        //implement when orders are set up
        val totalSales = binding.totalSales
        val currentOrders = binding.currentOrders

        val itemView = layoutInflater.inflate(R.layout.layout_edit_food_stall_details, null)
        val editStallImage = itemView.findViewById<ImageView>(R.id.editStallImage)
        var stallImageUri: Uri? = null

        val cardImageResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    stallImageUri = data!!.data
                    editStallImage.setImageURI(stallImageUri)
                }
            }


        editStallDetails.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val editStallName = itemView.findViewById<EditText>(R.id.stallName)
            val editStallAddress = itemView.findViewById<EditText>(R.id.stallAddress)
            val editStallPhone = itemView.findViewById<EditText>(R.id.stallPhone)
            val editStallDescription = itemView.findViewById<EditText>(R.id.stallDescription)
            val editStallImageCard =
                itemView.findViewById<MaterialCardView>(R.id.editStallImageCard)

            editStallName.setText(stallName.text)
            editStallAddress.setText(stallAddress.text)
            editStallPhone.setText(stallPhone.text)
            editStallDescription.setText(stallDescription.text)
            Glide.with(activity!!).load(Common.foodStallSelected!!.image).into(editStallImage)

            editStallImageCard.setOnClickListener {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                cardImageResultLauncher.launch(intent)
            }

            builder.setNegativeButton("CANCEL") { dialogInterface, _ -> dialogInterface.dismiss() }
            builder.setPositiveButton("OK") { dialogInterface, _ ->
                val firstNums = arrayOf('6', '8', '9')
                val newStallName = editStallName.text.toString().trim()
                val newStallPhone = editStallPhone.text.toString().trim()
                val newStallAddress = editStallAddress.text.toString().trim()
                val newStallDescription = editStallDescription.text.toString().trim()

                if (newStallName.isEmpty() || newStallPhone.isEmpty() || newStallAddress.isEmpty() || newStallDescription.isEmpty())
                    Toast.makeText(
                        requireContext(),
                        "Empty fields are not allowed!!",
                        Toast.LENGTH_SHORT
                    ).show()
                else if (!Arrays.stream(firstNums).anyMatch { t -> t == newStallPhone[0] } || newStallPhone.length != 8)
                    Toast.makeText(requireContext(), "Invalid phone number", Toast.LENGTH_SHORT).show()
                else {
                    dialog.setMessage("Uploading")
                    dialog.show()

                    val imageName = Common.currentUser!!.uid
                    val imageFolder = storageRef.child("stallImage/$imageName")
                    val updateData = HashMap<String, Any>()
                    updateData["name"] = newStallName
                    updateData["address"] = newStallAddress
                    updateData["phone"] = newStallPhone
                    updateData["description"] = newStallDescription

                    if (stallImageUri == null) {
                        foodStallRef.child(Common.foodStallSelected!!.key!!)
                            .updateChildren(updateData)
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                            .addOnCompleteListener { _ ->
                                Common.foodStallSelected!!.name = newStallName
                                Common.foodStallSelected!!.address = newStallAddress
                                Common.foodStallSelected!!.phone = newStallPhone
                                Common.foodStallSelected!!.description = newStallDescription

                                stallName.text = Common.foodStallSelected!!.name
                                stallAddress.text = Common.foodStallSelected!!.address
                                stallPhone.text = Common.foodStallSelected!!.phone
                                stallDescription.text = Common.foodStallSelected!!.description

                                Toast.makeText(
                                    requireContext(),
                                    "Edit Stall Success",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        dialog.dismiss()
                    }
                    else {
                        imageFolder.putFile(stallImageUri!!).addOnFailureListener {
                            dialog.dismiss()
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }.addOnProgressListener {
                            val progress = 100.0 * it.bytesTransferred / it.totalByteCount
                            dialog.setMessage("Uploaded $progress%")
                        }.addOnSuccessListener {
                            imageFolder.downloadUrl.addOnSuccessListener {
                                updateData["image"] = it.toString()
                                foodStallRef.child(Common.foodStallSelected!!.key!!)
                                    .updateChildren(updateData)
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            requireContext(),
                                            e.message,
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                    .addOnCompleteListener { _ ->
                                        Common.foodStallSelected!!.image = it.toString()
                                        Common.foodStallSelected!!.name = newStallName
                                        Common.foodStallSelected!!.address = newStallAddress
                                        Common.foodStallSelected!!.phone = newStallPhone
                                        Common.foodStallSelected!!.description = newStallDescription

                                        stallName.text = Common.foodStallSelected!!.name
                                        stallAddress.text = Common.foodStallSelected!!.address
                                        stallPhone.text = Common.foodStallSelected!!.phone
                                        stallDescription.text =
                                            Common.foodStallSelected!!.description
                                        Glide.with(activity!!)
                                            .load(Common.foodStallSelected!!.image)
                                            .into(stallImage)

                                        Toast.makeText(
                                            requireContext(),
                                            "Edit Stall Success",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                dialog.dismiss()
                            }
                        }
                    }
                    dialogInterface.dismiss()
                }
            }
            if (itemView.parent != null)
                (itemView.parent as ViewGroup).removeView(itemView)
            builder.setView(itemView)
            val uploadDialog = builder.create()
            uploadDialog.show()
        }



        filterPopularFood.setOnClickListener {
            //filter by rating or sales
        }

        dialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()
        dialog.show()
        recyclerViewPopularFood.layoutManager = LinearLayoutManager(context)
        val layoutAnimationController =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)

        vendorHomeViewModel.foodList.observe(viewLifecycleOwner) {
            dialog.dismiss()
            recyclerViewPopularFood.adapter = VendorFoodListAdapter(requireContext(), it.toMutableList())
            recyclerViewPopularFood.layoutAnimation = layoutAnimationController
        }

        swipeRefreshLayout.setOnRefreshListener {
            vendorHomeViewModel.loadFoodList()
            swipeRefreshLayout.isRefreshing = false
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}