package com.example.zeroenqueue.uiVendor.orders

import android.content.Context.DISPLAY_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.VendorMyOrderAdapter
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.common.SwipeHelper
import com.example.zeroenqueue.databinding.FragmentOrdersBinding
import com.example.zeroenqueue.eventBus.CountCartEvent
import com.example.zeroenqueue.interfaces.IDeleteBtnCallback
import com.google.firebase.database.FirebaseDatabase
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_cart.*
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder

class OrdersFragment : Fragment() {

//    private var _binding: FragmentOrdersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
//    private val binding get() = _binding!!

    lateinit var recycler_order: RecyclerView
    lateinit var layoutAnimationController: LayoutAnimationController
    lateinit var ordersViewModel: OrdersViewModel

    private var adapter: VendorMyOrderAdapter ?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


//        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
//        val root: View = binding.root
        val root = inflater.inflate(R.layout.fragment_orders, container, false)
        initView(root)

        ordersViewModel = ViewModelProvider(this).get(OrdersViewModel::class.java)

        ordersViewModel!!.messageError.observe(viewLifecycleOwner, Observer { s ->
            Toast.makeText(context, s , Toast.LENGTH_SHORT).show()
        })

        ordersViewModel!!.getOrderModelList().observe(viewLifecycleOwner, Observer { orderList ->
            if(orderList != null) {
                adapter = VendorMyOrderAdapter(requireContext(), orderList.toMutableList())
                recycler_order.adapter = adapter
                recycler_order.layoutAnimation = layoutAnimationController
            }
        })
//        val textView: TextView = binding.textGallery
//        ordersViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    private fun initView(root: View) {
        recycler_order = root.findViewById(R.id.recycler_order) as RecyclerView
        recycler_order.setHasFixedSize(true)
        recycler_order.layoutManager = LinearLayoutManager(context)

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.item_animation_from_left)

        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels

        val swipe = object : SwipeHelper(requireContext(), recycler_order!!, 200) {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {
                buffer.add(MyButton(context!!,
                    "Add On",
                    30,
                    0,
                    Color.parseColor("#FF3c30"),
                    object: IDeleteBtnCallback {
                        override fun onClick(pos: Int) {

                    }


                }))

                buffer.add(MyButton(context!!,
                    "Call",
                    30,
                    0,
                    Color.parseColor("#560027"),
                    object: IDeleteBtnCallback {
                        override fun onClick(pos: Int) {
                            Dexter.withActivity(activity)
                                .withPermission(android.Manifest.permission.CALL_PHONE)
                                .withListener(object: PermissionListener {
                                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                                        val orderModel = adapter!!.getItemAtPosition(pos)
                                        val intent = Intent()
                                        intent.setAction(Intent.ACTION_DIAL)
                                        intent.setData(Uri.parse(StringBuilder("tel: ")
                                            .append(orderModel.userPhone).toString()))
                                        startActivity(intent)
                                    }

                                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                                        Toast.makeText(context, "You must accept this permission " + response!!.permissionName, Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onPermissionRationaleShouldBeShown(
                                        permission: PermissionRequest?,
                                        token: PermissionToken?
                                    ) {
                                    }

                                }).check()
                        }


                    }))

                buffer.add(MyButton(context!!,
                    "Edit",
                    30,
                    0,
                    Color.parseColor("#FF3c30"),
                    object: IDeleteBtnCallback {
                        override fun onClick(pos: Int) {

                        }


                    }))

                buffer.add(MyButton(context!!,
                    "Delete",
                    30,
                    0,
                    Color.parseColor("#FF3c30"),
                    object: IDeleteBtnCallback {
                        override fun onClick(pos: Int) {
                            val orderModel = adapter!!.getItemAtPosition(pos)

                            val builder = AlertDialog.Builder(context!!)
                                .setTitle("Delete")
                                .setMessage("Do you really want to delete this order?")
                                .setNegativeButton("CANCEL") {
                                    dialogInterface, i -> dialogInterface.dismiss()
                                }
                                .setPositiveButton("DELETE") {
                                    dialogInterface, i -> FirebaseDatabase.getInstance()
                                    .getReference(Common.ORDER_REF)
                                    .child(orderModel!!.key!!)
                                    .removeValue()
                                    .addOnFailureListener {
                                        Toast.makeText(context!!, "" + it.message, Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnSuccessListener {
                                        adapter!!.removeItem(pos)
                                        adapter!!.notifyItemRemoved(pos)
//                                        txt_order_filter.setText(StringBuilder("Orders (")
//                                            .append(adapter!!.itemCount)
//                                            .append(")"))
                                        dialogInterface.dismiss()
                                        Toast.makeText(context!!, "Order has been deleted", Toast.LENGTH_SHORT).show()
                                    }
                                }

                            val dialog = builder.create()
                            dialog.show()

                            val btn_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                            btn_negative.setTextColor(Color.LTGRAY)
                            val btn_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                            btn_positive.setTextColor(Color.RED)
                        }


                    }))
        }
    }
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}