package com.example.zeroenqueue.uiVendor.orders

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zeroenqueue.Notifications.IFCMService
import com.example.zeroenqueue.Notifications.RetrofitFCMClient
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.VendorMyOrderAdapter
import com.example.zeroenqueue.classes.FCMSendData
import com.example.zeroenqueue.classes.Order
import com.example.zeroenqueue.classes.TokenModel
import com.example.zeroenqueue.common.BottomSheetOrderFragment
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.common.SwipeHelper
import com.example.zeroenqueue.databinding.FragmentVendorOrderSummaryBinding
import com.example.zeroenqueue.eventBus.CountCartEvent
import com.example.zeroenqueue.eventBus.LoadAllOrders
import com.example.zeroenqueue.eventBus.LoadOrderEvent
import com.example.zeroenqueue.eventBus.MenuItemBack
import com.example.zeroenqueue.interfaces.IDeleteBtnCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dmax.dialog.SpotsDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_vendor_order_summary.*
import kotlinx.android.synthetic.main.layout_dialog_cancelled.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class VendorOrderSummaryFragment : Fragment() {

    private var _binding: FragmentVendorOrderSummaryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var vendorOrderSummaryViewModel: VendorOrderSummaryViewModel
    private val compositeDisposable = CompositeDisposable()
    lateinit var ifcmService: IFCMService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        ifcmService = RetrofitFCMClient.getInstance().create(IFCMService::class.java)
        @Suppress("DEPRECATION")
        setHasOptionsMenu(true)

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

        val buttonWidth: Int
        val textSize: Int

        val density = resources.displayMetrics.density
        if (density >= 4.0) {
            buttonWidth = 200
            textSize = 30
        } else if (density >= 3.0) {
            buttonWidth = 180
            textSize = 27
        } else if (density >= 2.0) {
            buttonWidth = 160
            textSize = 24
        } else if (density >= 1.5) {
            buttonWidth = 140
            textSize = 21
        } else if (density >= 1.0) {
            buttonWidth = 120
            textSize = 18
        } else {
            buttonWidth = 100
            textSize = 15
        }


        val swipe = object : SwipeHelper(requireContext(), recycler_order, buttonWidth) {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {
                buffer.add(
                    MyButton(context!!, "Call", textSize, 0, Color.parseColor("#560027"),
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
                    MyButton(context!!, "Edit", textSize, 0, Color.parseColor("#FF3c30"),
                        object : IDeleteBtnCallback {
                            override fun onClick(pos: Int) {
                                showEditDialog(adapter!!.getItemAtPosition(pos), pos)
                            }
                        })
                )

                buffer.add(
                    MyButton(context!!, "Delete", textSize, 0, Color.parseColor("#FF3c30"),
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
                                                    it.message,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            .addOnSuccessListener {
                                                adapter!!.removeItem(pos)
                                                adapter!!.notifyItemRemoved(pos)
                                                updateTextCounter()
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

            private fun showEditDialog(order: Order, pos: Int) {
                val layout_dialog: View?
                val builder: AlertDialog.Builder?

                var preparing:RadioButton?=null
                var completed:RadioButton?=null
                var delete:RadioButton?=null
                var placed:RadioButton?=null
                var cancelled:RadioButton?=null

                when (order.orderStatus) {
                    3 -> {
                        layout_dialog = LayoutInflater.from(context!!)
                            .inflate(R.layout.layout_dialog_cancelled, null)
                        builder = AlertDialog.Builder(context!!)
                            .setView(layout_dialog)

                        delete = layout_dialog.findViewById<View>(R.id.delete) as RadioButton
                        placed = layout_dialog.findViewById<View>(R.id.placed) as RadioButton
                    }
                    0 -> {
                        layout_dialog = LayoutInflater.from(context!!)
                            .inflate(R.layout.layout_dialog_preparing, null)
                        builder = AlertDialog.Builder(context!!, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
                            .setView(layout_dialog)

                        preparing = layout_dialog.findViewById<View>(R.id.preparing) as RadioButton
                        cancelled = layout_dialog.findViewById<View>(R.id.cancelled) as RadioButton
                    }
                    else -> {
                        layout_dialog = LayoutInflater.from(context!!)
                            .inflate(R.layout.layout_dialog_completed, null)
                        builder = AlertDialog.Builder(context!!)
                            .setView(layout_dialog)

                        completed = layout_dialog.findViewById<View>(R.id.completed) as RadioButton
                        cancelled = layout_dialog.findViewById<View>(R.id.cancelled) as RadioButton
                    }
                }

                val btn_ok = layout_dialog.findViewById<View>(R.id.okay) as Button
                val btn_cancel = layout_dialog.findViewById<View>(R.id.cancel) as Button

                val status = layout_dialog.findViewById<View>(R.id.status) as TextView

                status.text = StringBuilder("Order Status(")
                    .append(Common.convertStatusToText(order.orderStatus))
                    .append(")")

                val dialog = builder.create()
                dialog.show()
                btn_cancel.setOnClickListener { dialog.dismiss() }
                btn_ok.setOnClickListener {
                    dialog.dismiss()
                    if(cancelled != null && cancelled.isChecked) {
                        updateOrder(pos, order, 3)
                    } else if(preparing != null && preparing.isChecked) {
                        updateOrder(pos, order, 1)
                    } else if(completed != null && completed.isChecked) {
                        updateOrder(pos, order, 2)
                    } else if(placed != null && placed.isChecked) {
                        updateOrder(pos, order, 0)
                    } else if(delete != null && delete.isChecked) {
                        deleteOrder(pos, order)
                    }
                }

            }

            private fun deleteOrder(pos: Int, order: Order) {
                if(!TextUtils.isEmpty(order.key)) {

                    FirebaseDatabase.getInstance()
                        .getReference(Common.ORDER_REF)
                        .child(order.key!!)
                        .removeValue()
                        .addOnFailureListener { throwable -> Toast.makeText(context!!, "" + throwable.message,
                            Toast.LENGTH_SHORT).show() }
                        .addOnSuccessListener {
                            adapter!!.removeItem(pos)
                            adapter!!.notifyItemRemoved(pos)
                            updateTextCounter()
                            Toast.makeText(context!!, "Update order success!",
                                Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context!!, "Order number must not be empty!",
                        Toast.LENGTH_SHORT).show()
                }
            }

            private fun updateOrder(pos: Int, order: Order, i: Int) {
                if(!TextUtils.isEmpty(order.key)) {
                    val update_data = HashMap<String, Any>()
                    update_data["orderStatus"] = i

                    FirebaseDatabase.getInstance()
                        .getReference(Common.ORDER_REF)
                        .child(order.key!!)
                        .updateChildren(update_data)
                        .addOnFailureListener { throwable -> Toast.makeText(context!!, "" + throwable.message,
                            Toast.LENGTH_SHORT).show() }
                        .addOnSuccessListener {

                            val dialog = SpotsDialog.Builder().setContext(context!!).setCancelable(false).build()
                            dialog.show()

                            FirebaseDatabase.getInstance()
                                .getReference(Common.TOKEN_REF)
                                .child(order.userId!!)
                                .addListenerForSingleValueEvent(object:ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if(snapshot.exists()) {
                                            val tokenModel = snapshot.getValue(TokenModel::class.java)
                                            val notiData = HashMap<String, String>()
                                            notiData.put(Common.NOTI_TITLE, "Your order was updated")
                                            notiData.put(Common.NOTI_CONTENT, StringBuilder("Your order ")
                                                .append(order.key)
                                                .append(" was update to ")
                                                .append(Common.convertStatusToText(status.toString().toInt())).toString())

                                            val sendData = FCMSendData(tokenModel!!.token!!, notiData)

                                            compositeDisposable.add(
                                                ifcmService.sendNotification(sendData)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(
                                                        { fcmResponse ->
                                                            if (fcmResponse.success == 1) {
                                                                Toast.makeText(context, "Update order successful", Toast.LENGTH_SHORT).show()
                                                            } else {
                                                                Toast.makeText(
                                                                    context,
                                                                    "Notification failed",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        }, { t ->
                                                            dialog.dismiss()
                                                            Toast.makeText(context, ""+t.message, Toast.LENGTH_SHORT).show()
                                                        })
                                            )
                                        } else {
                                            dialog.dismiss()
                                            Toast.makeText(context, "Token not found", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        dialog.dismiss()
                                        Toast.makeText(context, "" + error.message, Toast.LENGTH_SHORT).show()
                                    }

                                })
                            adapter!!.removeItem(pos)
                            adapter!!.notifyItemRemoved(pos)
                            updateTextCounter()
                            Toast.makeText(context!!, "Update order success!",
                                Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context!!, "Order number must not be empty!",
                        Toast.LENGTH_SHORT).show()
                }
            }

            private fun updateTextCounter() {
                txt_order_filter.text = StringBuilder("Orders (")
                    .append(adapter!!.itemCount)
                    .append(")")
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

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        @Suppress("DEPRECATION")
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.vendor_order_list, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        @Suppress("DEPRECATION")
        super.onOptionsItemSelected(item)
        if (item.itemId == R.id.filter_orders) {
            val bottomSheet = BottomSheetOrderFragment.instance
            bottomSheet!!.show(requireActivity().supportFragmentManager, "OrderList")
            return true
        }
        return false
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

        compositeDisposable.clear()
        super.onStop()
    }

    override fun onDestroy() {
        EventBus.getDefault().postSticky(CountCartEvent(true))
        EventBus.getDefault().postSticky(MenuItemBack())
        super.onDestroy()
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onLoadAllOrders(event: LoadAllOrders) {
        vendorOrderSummaryViewModel.loadAllOrders()
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