package com.example.zeroenqueue.uiCustomer.discounts

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.DiscountsAdapter
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.FragmentDiscountsBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dmax.dialog.SpotsDialog
import kotlin.collections.HashMap

class DiscountsFragment : Fragment() {

    private var _binding: FragmentDiscountsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val storageRef = FirebaseStorage.getInstance().reference
    private val userRef = FirebaseDatabase.getInstance().getReference(Common.USER_REF)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val discountsViewModel = ViewModelProvider(this)[DiscountsViewModel::class.java]

        _binding = FragmentDiscountsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerViewDiscounts = binding.recyclerDiscounts
        val swipeRefreshLayout = binding.swipeRefresh
        val noDiscount = binding.NoDiscounts
        val notNUSLayout = binding.notNUSLayout
        val btnUploadNUSCard = binding.btnUploadNUSCard

        val dialog: AlertDialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()
        dialog.show()
        recyclerViewDiscounts.setHasFixedSize(true)
        recyclerViewDiscounts.layoutManager = LinearLayoutManager(context)
        val layoutAnimationController: LayoutAnimationController =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)

        swipeRefreshLayout.setOnRefreshListener {
            discountsViewModel.loadDiscountList()
            swipeRefreshLayout.isRefreshing = false
        }

        discountsViewModel.discountList.observe(viewLifecycleOwner) {
            if (!Common.currentUser!!.nus) {
                recyclerViewDiscounts.visibility = View.GONE
                notNUSLayout.visibility = View.VISIBLE
            }
            else if (it == null || it.isEmpty()) {
                recyclerViewDiscounts.visibility = View.GONE
                noDiscount.visibility = View.VISIBLE
            }
            else {
                recyclerViewDiscounts.adapter = DiscountsAdapter(requireContext(), it)
                recyclerViewDiscounts.layoutAnimation = layoutAnimationController
            }
            dialog.dismiss()
        }

        val itemView = layoutInflater.inflate(R.layout.layout_upload_nus_card, null)
        val cardImage = itemView.findViewById<ImageView>(R.id.addNUSCardImage)
        val cardImagePrompt = itemView.findViewById<TextView>(R.id.add_card_image_text)
        var cardImageUri : Uri? = null

        val cardImageResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    cardImageUri = data!!.data
                    cardImage.setImageURI(cardImageUri)
                    cardImagePrompt.text = "Change image..."
                }
            }

        btnUploadNUSCard.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Upload NUS card image")
            builder.setMessage("Please upload your NUS card here")

            cardImage.setOnClickListener {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                cardImageResultLauncher.launch(intent)
            }

            builder.setNegativeButton("CANCEL") {dialogInterface, _ -> dialogInterface.dismiss()}
            builder.setPositiveButton("OK") {dialogInterface, _ ->
                if (cardImageUri != null) {
                    dialog.setMessage("Uploading")
                    dialog.show()

                    val imageName = Common.currentUser!!.uid
                    val imageFolder = storageRef.child("NUSValidation/$imageName")
                    imageFolder.putFile(cardImageUri!!).addOnFailureListener {
                        dialog.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }.addOnProgressListener {
                        val progress = 100.0 * it.bytesTransferred/ it.totalByteCount
                        dialog.setMessage("Uploaded $progress%")
                    }.addOnSuccessListener {
                        dialogInterface.dismiss()
                        imageFolder.downloadUrl.addOnSuccessListener {
                            val updateData = HashMap<String, Any>()
                            updateData["cardImage"] = it.toString()
                            updateData["nus"] = true
                            userRef.child(Common.currentUser!!.uid!!).updateChildren(updateData)
                                .addOnFailureListener { e ->
                                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                                }
                                .addOnCompleteListener { _ ->
                                    Common.currentUser!!.cardImage = it.toString()
                                    Common.currentUser!!.nus = true
                                    notNUSLayout.visibility = View.GONE
                                    discountsViewModel.loadDiscountList()
                                    Toast.makeText(requireContext(), "Upload Success", Toast.LENGTH_SHORT).show()
                                }
                            dialog.dismiss()
                        }
                    }
                }
            }

            builder.setView(itemView)
            val uploadDialog = builder.create()
            uploadDialog.show()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}