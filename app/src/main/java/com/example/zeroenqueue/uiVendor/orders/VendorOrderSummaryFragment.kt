package com.example.zeroenqueue.uiVendor.orders

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.VendorMyOrderAdapter
import com.example.zeroenqueue.common.BottomSheetOrderFragment
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.common.SwipeHelper
import com.example.zeroenqueue.databinding.FragmentVendorOrderSummaryBinding
import com.example.zeroenqueue.eventBus.CountCartEvent
import com.example.zeroenqueue.eventBus.LoadOrderEvent
import com.example.zeroenqueue.interfaces.IDeleteBtnCallback
import com.google.firebase.database.FirebaseDatabase
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.fragment_vendor_order_summary.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class VendorOrderSummaryFragment : Fragment() {

    private var _binding: FragmentVendorOrderSummaryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var vendorOrderSummaryViewModel: VendorOrderSummaryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        vendorOrderSummaryViewModel = ViewModelProvider(this)[VendorOrderSummaryViewModel::class.java]

        _binding = FragmentVendorOrderSummaryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recycler_order = binding.recyclerOrder
        var adapter: VendorMyOrderAdapter? = null
        recycler_order.setHasFixedSize(true)
        recycler_order.layoutManager = LinearLayoutManager(context)
        val layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)

        val displayMetrics = DisplayMetrics()
        val version = android.os.Build.VERSION.SDK_INT
        var width = 0
        if (version >= android.os.Build.VERSION_CODES.S) {
            val windowMetrics = requireActivity().windowManager.currentWindowMetrics
            val bounds: Rect = windowMetrics.bounds
            width = bounds.width()
        }
        if (version == android.os.Build.VERSION_CODES.R) {
            @Suppress("DEPRECATION")
            requireActivity().display?.getRealMetrics(displayMetrics)
            width = displayMetrics.widthPixels
        }
        if (version < android.os.Build.VERSION_CODES.R) {
            @Suppress("DEPRECATION")
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            width = displayMetrics.widthPixels
        }

        val swipe = object : SwipeHelper(requireContext(), recycler_order, 200) {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {
                buffer.add(
                    MyButton(context!!, "Add On", 30, 0, Color.parseColor("#FF3c30"),
                        object : IDeleteBtnCallback {
                            override fun onClick(pos: Int) {
                            }
                        })
                )

                buffer.add(
                    MyButton(context!!, "Call", 30, 0, Color.parseColor("#560027"),
                        object : IDeleteBtnCallback {
                            override fun onClick(pos: Int) {
                                Dexter.withActivity(activity)
                                    .withPermission(android.Manifest.permission.CALL_PHONE)
                                    .withListener(object : PermissionListener {
                                        override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                                            val orderModel = adapter!!.getItemAtPosition(pos)
                                            val intent = Intent()
                                            intent.action = Intent.ACTION_DIAL
                                            intent.data = Uri.parse(
                                                StringBuilder("tel: ")
                                                    .append(orderModel.userPhone).toString()
                                            )
                                            startActivity(intent)
                                        }

                                        override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                                            Toast.makeText(
                                                context,
                                                "You must accept this permission " + response!!.permissionName,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        override fun onPermissionRationaleShouldBeShown(
                                            permission: PermissionRequest?,
                                            token: PermissionToken?
                                        ) {
                                        }
                                    }).check()
                            }
                        })
                )

                buffer.add(
                    MyButton(context!!, "Edit", 30, 0, Color.parseColor("#FF3c30"),
                        object : IDeleteBtnCallback {
                            override fun onClick(pos: Int) {
                            }
                        })
                )

                buffer.add(
                    MyButton(context!!, "Delete", 30, 0, Color.parseColor("#FF3c30"),
                        object : IDeleteBtnCallback {
                            override fun onClick(pos: Int) {
                                val orderModel = adapter!!.getItemAtPosition(pos)
                                val builder = AlertDialog.Builder(context!!)
                                    .setTitle("Delete")
                                    .setMessage("Do you really want to delete this order?")
                                    .setNegativeButton("CANCEL") { dialogInterface, _ ->
                                        dialogInterface.dismiss()
                                    }
                                    .setPositiveButton("DELETE") { dialogInterface, _ ->
                                        FirebaseDatabase.getInstance()
                                            .getReference(Common.ORDER_REF)
                                            .child(orderModel.key!!)
                                            .removeValue()
                                            .addOnFailureListener {
                                                Toast.makeText(
                                                    context!!,
                                                    "" + it.message,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            .addOnSuccessListener {
                                                adapter!!.removeItem(pos)
                                                adapter!!.notifyItemRemoved(pos)
//                                        txt_order_filter.setText(StringBuilder("Orders (")
//                                            .append(adapter!!.itemCount)
//                                            .append(")"))
                                                dialogInterface.dismiss()
                                                Toast.makeText(
                                                    context!!,
                                                    "Order has been deleted",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    }

                                val dialog = builder.create()
                                dialog.show()

                                val btn_negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                                btn_negative.setTextColor(Color.LTGRAY)
                                val btn_positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                                btn_positive.setTextColor(Color.RED)
                            }
                        })
                )
            }
        }

        vendorOrderSummaryViewModel.messageError.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }

        vendorOrderSummaryViewModel.getOrderList().observe(viewLifecycleOwner) {
            if (it != null) {
                adapter = VendorMyOrderAdapter(requireContext(), it.toMutableList())
                recycler_order.adapter = adapter
                recycler_order.layoutAnimation = layoutAnimationController
                txt_order_filter.text = StringBuilder("Orders (")
                    .append(it.size)
                    .append(")")
            }
        }
        return root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.vendor_order_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.filter_orders) {
            val bottomSheet = BottomSheetOrderFragment.instance
            bottomSheet!!.show(requireActivity().supportFragmentManager, "OrderList")
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    override fun onStop() {
        if (EventBus.getDefault().hasSubscriberForEvent((LoadOrderEvent::class.java)))
            EventBus.getDefault().removeStickyEvent(LoadOrderEvent::class.java)
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onDestroy() {
        EventBus.getDefault().postSticky(CountCartEvent(true))
        super.onDestroy()
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onLoadOrder(event: LoadOrderEvent) {
        vendorOrderSummaryViewModel.loadOrder(event.status)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}