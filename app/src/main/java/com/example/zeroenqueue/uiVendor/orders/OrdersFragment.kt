package com.example.zeroenqueue.uiVendor.orders

import android.content.Context.DISPLAY_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.view.View.inflate
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.resources.Compatibility.Api21Impl.inflate
import androidx.core.content.res.ColorStateListInflaterCompat.inflate
import androidx.core.content.res.ComplexColorCompat.inflate
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
import com.example.zeroenqueue.databinding.ActivityCreateNewStallBinding.inflate
import com.example.zeroenqueue.databinding.ActivityMainCustomerBinding.inflate
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
import android.view.LayoutInflater
import com.example.zeroenqueue.common.BottomSheetOrderFragment
import com.example.zeroenqueue.eventBus.FoodItemClick
import com.example.zeroenqueue.eventBus.LoadOrderEvent
import com.example.zeroenqueue.eventBus.UpdateCartItems
import kotlinx.android.synthetic.main.fragment_order_summary_vendor.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.text.StringBuilder

class OrdersFragment : Fragment() {

    lateinit var recycler_order: RecyclerView
    lateinit var layoutAnimationController: LayoutAnimationController
    lateinit var ordersViewModel: OrdersViewModel

    private var adapter: VendorMyOrderAdapter ?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val root = inflater.inflate(R.layout.fragment_order_summary_vendor, container, false)
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
                txt_order_filter.setText(StringBuilder("Orders (")
                    .append(orderList.size)
                    .append(")"))
            }
        })
//        val textView: TextView = binding.textGallery
//        ordersViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    private fun initView(root: View) {

        setHasOptionsMenu(true);

        recycler_order = root.findViewById(R.id.recycler_order) as RecyclerView
        recycler_order.setHasFixedSize(true)
        recycler_order.layoutManager = LinearLayoutManager(context)

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_item_from_left)

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.vendor_order_list, menu);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.filter_orders) {
            val bottomSheet = BottomSheetOrderFragment.instance
            bottomSheet!!.show(requireActivity().supportFragmentManager, "OrderList")
        }
        return true;
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
        ordersViewModel.loadOrder(event.status)
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}