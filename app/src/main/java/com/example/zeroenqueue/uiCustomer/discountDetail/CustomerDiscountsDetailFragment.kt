package com.example.zeroenqueue.uiCustomer.discountDetail

import android.app.AlertDialog
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.zeroenqueue.classes.Discount
import com.example.zeroenqueue.classes.User
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.FragmentCustomerDiscountsDetailBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dmax.dialog.SpotsDialog
import java.text.SimpleDateFormat

class CustomerDiscountsDetailFragment : Fragment() {

    private var _binding: FragmentCustomerDiscountsDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val currentUserRef = FirebaseDatabase.getInstance().getReference(Common.USER_REF)
        .child(Common.currentUser!!.uid!!)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val customerDiscountsDetailViewModel =
            ViewModelProvider(this)[CustomerDiscountsDetailViewModel::class.java]

        _binding = FragmentCustomerDiscountsDetailBinding.inflate(inflater, container, false)

        val root: View = binding.root
        val discount = binding.discount
        val foodImage = binding.foodImage
        val foodName = binding.foodName
        val foodStall = binding.foodStall
        val oldPrice = binding.oldFoodPrice
        val newPrice = binding.newFoodPrice
        val discountDescription = binding.discountDescription
        val expiry = binding.discountExpiry
        val btnRedeem = binding.btnRedeem

        customerDiscountsDetailViewModel.discountDetail.observe(viewLifecycleOwner) {
            val simpleDateFormat = SimpleDateFormat("dd/mm/yyyy")
            discount.text = StringBuilder(it.discount.toString()).append("%")
            context?.let { context -> Glide.with(context).load(it.foodImage).into(foodImage) }
            foodName.text = it.foodName
            foodStall.text = it.foodStallName
            oldPrice.text = StringBuilder("$").append(Common.formatPrice(it.oldPrice))
            oldPrice.paintFlags = oldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            newPrice.text = StringBuilder("$").append(Common.formatPrice(it.newPrice))
            discountDescription.text = it.description
            expiry.text = StringBuilder("Valid till ").append(simpleDateFormat.format(it.expiry))
        }

        btnRedeem.setOnClickListener {
            if (Common.currentUser!!.redeemedDiscounts!!.contains(Common.discountSelected!!))
                Toast.makeText(
                    requireContext(),
                    "Discount has already been redeemed!",
                    Toast.LENGTH_SHORT
                ).show()
            else {
                val dialog: AlertDialog =
                    SpotsDialog.Builder().setContext(context).setCancelable(false).build()
                dialog.show()
                currentUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val user = snapshot.getValue(User::class.java)
                                Common.currentUser!!.redeemedDiscounts!!.add(Common.discountSelected!!)
                                var newDiscounts = ArrayList<Discount>()
                                if (user!!.redeemedDiscounts != null)
                                    newDiscounts = user.redeemedDiscounts!!
                                if (newDiscounts.contains(Common.discountSelected)) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Discount has already been redeemed!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    dialog.dismiss()
                                }
                                else {
                                    newDiscounts.add(Common.discountSelected!!)
                                    val updateData = HashMap<String, Any>()
                                    updateData["redeemedDiscounts"] = newDiscounts
                                    snapshot.ref
                                        .updateChildren(updateData)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful)
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Discount redeemed successfully",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            dialog.dismiss()
                                        }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT)
                                .show()
                            dialog.dismiss()
                        }
                    })
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}