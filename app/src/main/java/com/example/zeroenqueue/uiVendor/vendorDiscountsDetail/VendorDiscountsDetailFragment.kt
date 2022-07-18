package com.example.zeroenqueue.uiVendor.vendorDiscountsDetail

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.zeroenqueue.R
import com.example.zeroenqueue.classes.Discount
import com.example.zeroenqueue.classes.FoodStall
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.FragmentVendorDiscountDetailsBinding
import com.example.zeroenqueue.interfaces.ILoadTimeFromFirebaseDiscountsCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*


class VendorDiscountsDetailFragment : Fragment(), ILoadTimeFromFirebaseDiscountsCallback {

    private var _binding: FragmentVendorDiscountDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var listener: ILoadTimeFromFirebaseDiscountsCallback
    private lateinit var navController: NavController
    private val cal : Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val vendorDiscountDetailsViewModel =
            ViewModelProvider(this)[VendorDiscountsDetailViewModel::class.java]

        _binding = FragmentVendorDiscountDetailsBinding.inflate(inflater, container, false)

        val root: View = binding.root
        val discountPicker = binding.discountPicker
        val foodImage = binding.foodImage
        val foodName = binding.foodName
        val foodStall = binding.foodStall
        val oldPrice = binding.oldFoodPrice
        val newPrice = binding.newFoodPrice
        val discountDescription = binding.inputDescription
        val expiryDatePicker = binding.datePicker
        val btnConfirmChanges = binding.btnConfirmChanges
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment_content_main_vendor)

        vendorDiscountDetailsViewModel.discountDetail.observe(viewLifecycleOwner) {
            if (Common.discountSelected != null) {
                context?.let { context -> Glide.with(context).load(it.foodImage).into(foodImage) }
                foodName.text = it.foodName
                foodStall.text = it.foodStallName
                discountPicker.value = it.discount
                oldPrice.text = StringBuilder("$").append(Common.formatPrice(it.oldPrice))
                oldPrice.paintFlags = oldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                newPrice.text = StringBuilder("$").append(Common.formatPrice(it.newPrice))
                discountDescription.setText(it.description)
                expiryDatePicker.text = simpleDateFormat.format(it.expiry).dropLast(9)
            }
            else {
                context?.let { context -> Glide.with(context).load(Common.foodSelected!!.image).into(foodImage) }
                foodName.text = Common.foodSelected!!.name
                foodStall.text = Common.foodStallSelected!!.name
                oldPrice.text = StringBuilder("$").append(Common.formatPrice(Common.foodSelected!!.price))
                oldPrice.paintFlags = oldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                newPrice.text = StringBuilder("$").append(Common.formatPrice(Common.foodSelected!!.price/2.0))
                expiryDatePicker.text = simpleDateFormat.format(System.currentTimeMillis() + 86400000).dropLast(9)
            }
        }

        val year: Int = cal.get(Calendar.YEAR)
        val month: Int = cal.get(Calendar.MONTH)
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)

        val dateSetListener =
            OnDateSetListener { _, year, month, day ->
                cal.set(year, month, day)
                expiryDatePicker.text = simpleDateFormat.format(cal.time).dropLast(9)
            }

        val datePickerDialog = DatePickerDialog(requireContext(), dateSetListener, year, month, day)
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() + 86400000

        listener = this

        expiryDatePicker.setOnClickListener {
            datePickerDialog.show()
        }

        discountPicker.setOnValueChangedListener { _, _, newVal ->
            val changedPrice = oldPrice.text.toString().drop(1).toDouble() * (1 - newVal/100.0)
            newPrice.text = StringBuilder("$").append(Common.formatPrice(changedPrice))
        }

        btnConfirmChanges.setOnClickListener {
            val discount = Discount()
            if (Common.discountSelected != null) {
                discount.id = Common.discountSelected!!.id
                discount.foodImage = Common.discountSelected!!.foodImage
                discount.foodName = Common.discountSelected!!.foodName
                discount.foodUid = Common.discountSelected!!.foodUid
            }
            else {
                discount.id = Common.randomTimeId()
                if (Common.foodSelected != null) {
                    discount.foodImage = Common.foodSelected!!.image
                    discount.foodName = Common.foodSelected!!.name
                    discount.foodUid = Common.foodSelected!!.id
                }
                else
                    Toast.makeText(requireContext(), "Unable to find food item", Toast.LENGTH_SHORT).show()
            }
            discount.foodStallUid = Common.foodStallSelected!!.id
            discount.foodStallName = Common.foodStallSelected!!.name
            discount.discount = discountPicker.value
            discount.description = discountDescription.text.toString().trim()
            discount.newPrice = newPrice.text.toString().drop(1).toDouble()
            discount.oldPrice = oldPrice.text.toString().drop(1).toDouble()
            syncLocalTimeWithServerTime(discount)
        }
        return root
    }


    private fun syncLocalTimeWithServerTime(discount: Discount) {
        val offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset")
        offsetRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                listener.onLoadTimeFailed(p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                val offset = p0.getValue(Long::class.java)
                val estimatedTimeMs = cal.timeInMillis + offset!!
                val sdf = SimpleDateFormat("MM dd yyyy, HH:mm")
                Log.d("ZE", "" + sdf.format(estimatedTimeMs))
                listener.onLoadTimeSuccess(discount, estimatedTimeMs)
            }
        })
    }

    override fun onLoadTimeSuccess(discount: Discount, estimatedTimeMs: Long) {
        discount.expiry = (estimatedTimeMs)
        submitToFirebase(discount)
    }

    override fun onLoadTimeFailed(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun submitToFirebase(discount: Discount) {
        FirebaseDatabase.getInstance()
            .getReference(Common.DISCOUNT_REF)
            .child(discount.id!!)
            .setValue(discount)
            .addOnFailureListener { e -> Toast.makeText(requireContext(), "" + e.message, Toast.LENGTH_SHORT).show()}
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Discount added successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.navigate(R.id.navigation_vendor_discounts)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Common.foodSelected = null
        Common.discountSelected = null
    }

}