package com.example.zeroenqueue.uiCustomer.useDiscounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.UseDiscountsAdapter
import com.example.zeroenqueue.classes.Discount
import com.example.zeroenqueue.classes.User
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.FragmentUseDiscountsBinding
import com.example.zeroenqueue.db.CartDataSource
import com.example.zeroenqueue.db.CartDatabase
import com.example.zeroenqueue.db.CartItem
import com.example.zeroenqueue.db.LocalCartDataSource
import com.example.zeroenqueue.eventBus.CountCartEvent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dmax.dialog.SpotsDialog
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus

class UseDiscountsFragment : Fragment() {
    private var _binding: FragmentUseDiscountsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val useDiscountRef = FirebaseDatabase.getInstance().getReference(Common.USER_REF)
                                    .child(Common.currentUser!!.uid!!)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val cartDataSource: CartDataSource = LocalCartDataSource(CartDatabase.getInstance(requireContext()).cartDAO())
        val useDiscountsViewModel =
            ViewModelProvider(this)[UseDiscountsViewModel::class.java]

        val navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment_content_main)

        _binding = FragmentUseDiscountsBinding.inflate(inflater, container, false)
        val root = binding.root
        val getDiscounts = binding.getDiscounts
        val noDiscounts = binding.noDiscounts
        val swipeRefresh = binding.swipeRefresh
        val recyclerDiscounts = binding.availableDiscounts
        val applyDiscount =  binding.applyDiscount
        recyclerDiscounts.layoutManager = LinearLayoutManager(context)
        val layoutAnimationController =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)

        val dialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()
        dialog.show()

        getDiscounts.setOnClickListener {
            navController.navigate(R.id.navigation_customerDiscounts)
            Common.cartItemSelected = null
        }

        applyDiscount.setOnClickListener {
            if (Common.discountSelected == null)
                Toast.makeText(requireContext(), "Please select a discount", Toast.LENGTH_SHORT).show()
            else {
                Common.cartItemSelected!!.discount = Common.discountSelected!!.discount / 100.0
                val cartItem = Common.cartItemSelected!!

                cartDataSource.getItemWithAllOptionsInCart(
                    Common.currentUser!!.uid!!,
                    cartItem.foodId,
                    cartItem.foodSize,
                    cartItem.foodAddon
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<CartItem> {
                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onSuccess(cartItemFromDB: CartItem) {
                            //if item is alr in db, update
                            if (cartItemFromDB == cartItem) {
                                cartItemFromDB.discount = cartItem.discount

                                cartDataSource.updateCart(cartItemFromDB)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(object : SingleObserver<Int> {
                                        override fun onSuccess(t: Int) {
                                            EventBus.getDefault().postSticky(CountCartEvent(true))
                                            useDiscountRef.addListenerForSingleValueEvent(object :
                                                ValueEventListener {
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    if (snapshot.exists()) {
                                                        val user = snapshot.getValue(User::class.java)
                                                        val discountList = user!!.redeemedDiscounts!!
                                                        for (i in 0 until discountList.size) {
                                                            if (discountList[i].id == Common.discountSelected!!.id) {
                                                                Common.discountSelected!!.isRedeemed = true
                                                                val discount = discountList[i]
                                                                discount.isRedeemed = true
                                                                useDiscountRef.child("redeemedDiscounts")
                                                                    .child(i.toString())
                                                                    .setValue(discount)
                                                                    .addOnCompleteListener { task ->
                                                                        if (task.isSuccessful) {
                                                                            Toast.makeText(
                                                                                context,
                                                                                "Applied discount successfully",
                                                                                Toast.LENGTH_SHORT
                                                                            ).show()
                                                                            navController.navigate(R.id.navigation_cart)
                                                                        }
                                                                    }
                                                                break
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

                                        override fun onSubscribe(d: Disposable) {

                                        }

                                        override fun onError(e: Throwable) {
                                            Toast.makeText(
                                                context,
                                                "[APPLY DISCOUNT]" + e.message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    })
                            } else {
                                Toast.makeText(context, "[NOT FOUND]", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onError(e: Throwable) {
                            Toast.makeText(context, "[CART ERROR]" + e.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
            }
        }

        swipeRefresh.setOnRefreshListener {
            useDiscountsViewModel.loadDiscountsList()
            swipeRefresh.isRefreshing = false
        }

        useDiscountsViewModel.discountList.observe(viewLifecycleOwner) {
            dialog.dismiss()
            if (it == null || it.isEmpty()) {
                noDiscounts.visibility = View.VISIBLE
                recyclerDiscounts.visibility = View.GONE
                applyDiscount.visibility = View.GONE
            }

            recyclerDiscounts.adapter = UseDiscountsAdapter(requireContext(), it)
            recyclerDiscounts.layoutAnimation = layoutAnimationController
        }

        return root
    }
}