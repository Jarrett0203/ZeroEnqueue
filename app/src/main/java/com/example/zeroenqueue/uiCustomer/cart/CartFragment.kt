package com.example.zeroenqueue.uiCustomer.cart

import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.MyCartAdapter
import com.example.zeroenqueue.classes.Order
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.common.SwipeHelper
import com.example.zeroenqueue.databinding.LayoutPlaceOrderBinding
import com.example.zeroenqueue.db.CartDataSource
import com.example.zeroenqueue.db.CartDatabase
import com.example.zeroenqueue.db.LocalCartDataSource
import com.example.zeroenqueue.eventBus.CountCartEvent
import com.example.zeroenqueue.eventBus.HideFABCart
import com.example.zeroenqueue.eventBus.UpdateCartItems
import com.example.zeroenqueue.interfaces.IDeleteBtnCallback
import com.example.zeroenqueue.interfaces.ILoadOrderCallbackListener
import com.example.zeroenqueue.interfaces.ILoadTimeFromFirebaseCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_cart.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*

class CartFragment: Fragment(), ILoadTimeFromFirebaseCallback {

    private lateinit var cartViewModel: CartViewModel
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var cartDataSource:CartDataSource?=null
    private var recyclerViewState: Parcelable?=null
    private lateinit var btn_place_order: Button

    var empty_cart: TextView?=null
    var total_prices: TextView?=null
    var group_place_holder: CardView?=null
    var recycler_cart:RecyclerView?=null
    var adapter: MyCartAdapter?=null
    var comments: TextView?=null

    lateinit var listener: ILoadTimeFromFirebaseCallback

    override fun onResume() {
        super.onResume()
        calculateTotalPrice()
    }

    override fun onCreateView (
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)

        EventBus.getDefault().postSticky(HideFABCart(true))
        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        cartViewModel.initCartDataSource(requireContext())
        val root = inflater.inflate(R.layout.fragment_cart, container, false)
        val rootComment = inflater.inflate(R.layout.layout_rating_comment, container, false)

        initViews(root, rootComment)
        cartViewModel.getMutableLiveDataCartItems().observe(viewLifecycleOwner) {
            if (it == null || it.isEmpty()) {
                recycler_cart!!.visibility = View.GONE
                group_place_holder!!.visibility = View.GONE
                empty_cart!!.visibility = View.VISIBLE
            } else {
                recycler_cart!!.visibility = View.VISIBLE
                group_place_holder!!.visibility = View.VISIBLE
                empty_cart!!.visibility = View.GONE

                adapter = MyCartAdapter(requireContext(), it)
                recycler_cart!!.adapter = adapter
            }
        }
        return root
    }

    private fun initViews(root:View, rootComment:View) {

        setHasOptionsMenu(true);

        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(requireContext()).cartDAO())

        listener = this
        recycler_cart = root.findViewById(R.id.recycler_cart) as RecyclerView
        recycler_cart!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recycler_cart!!.layoutManager = layoutManager
        recycler_cart!!.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))

        empty_cart = root.findViewById(R.id.txt_empty_cart) as TextView
        total_prices = root.findViewById(R.id.txt_total_price) as TextView
        group_place_holder = root.findViewById(R.id.group_place_holder) as CardView
        comments = rootComment.findViewById(R.id.edit_comment) as TextView

        val swipe = object : SwipeHelper(requireContext(), recycler_cart!!, 200) {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {
                buffer.add(MyButton(context!!,
                    "Delete",
                    30,
                    0,
                    Color.parseColor("#FF3c30"),
                    object: IDeleteBtnCallback {
                        override fun onClick(pos: Int) {
                            val itemDelete = adapter!!.getItemAtPosition(pos)
                            cartDataSource!!.deleteCart(itemDelete)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object:SingleObserver<Int> {
                                    override fun onSubscribe(d: Disposable) {

                                    }

                                    override fun onSuccess(t: Int) {
                                        adapter!!.notifyItemRemoved(pos)
                                        calculateTotalPrice()
                                        EventBus.getDefault().postSticky(CountCartEvent(true))
                                        Toast.makeText(context, "Delete item success", Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onError(e: Throwable) {
                                        Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()
                                    }

                                })
                        }
                    }

                ))
            }
        }

        btn_place_order = root.findViewById(R.id.btn_place_order) as Button

        btn_place_order.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("One more step!")

            val view = LayoutInflater.from(context).inflate(R.layout.layout_place_order, null)
            val collectionTime = view.findViewById<View>(R.id.time) as EditText
            val cash = view.findViewById<View>(R.id.cash) as RadioButton
            val braintree = view.findViewById<View>(R.id.braintree) as RadioButton

            collectionTime.setText(Common.currentUser!!.address)

            builder.setView(view)
            builder.setNegativeButton("No", {dialogInterface, _ -> dialogInterface.dismiss()})
                .setPositiveButton("YES", {dialogInterface, _ -> 
                    if(cash.isChecked)
                        paymentCash(collectionTime.text.toString(), comments!!.text.toString())
                })

            val dialog = builder.create()
            dialog.show()

        }
    }

    private fun calculateTotalPrice() {
        cartDataSource!!.totalPrice(Common.currentUser!!.uid!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Double> {
                override fun onSuccess(t: Double) {
                    total_prices!!.text = Common.formatPrice(t)
                    recycler_cart!!.layoutManager!!.onRestoreInstanceState(recyclerViewState)
                }

                override fun onSubscribe(d: Disposable) {
                }


                override fun onError(e: Throwable) {
                    if (!e.message!!.contains("empty"))
                        Toast.makeText(context, "[SUM CART]" + e.message, Toast.LENGTH_SHORT).show()
                }

            })
    }


    override fun onStart() {
        super.onStart()
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        cartViewModel.onStop()
        compositeDisposable.clear()
        EventBus.getDefault().postSticky(HideFABCart(false))
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onUpdateCartItems(event: UpdateCartItems) {
        recyclerViewState = recycler_cart!!.layoutManager!!.onSaveInstanceState()
        cartDataSource!!.updateCart(event.cartItem)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Int> {
                override fun onSuccess(t: Int) {
                    calculateTotalPrice()
                    recycler_cart!!.layoutManager!!.onRestoreInstanceState(recyclerViewState)
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(context, "[UPDATE CART]" + e.message, Toast.LENGTH_SHORT).show()
                }
            })

    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.cart_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu!!.findItem(R.id.action_settings).setVisible(false)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item!!.itemId == R.id.action_clear_cart) {
            cartDataSource!!.cleanCart((Common.currentUser!!.uid!!))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: SingleObserver<Int> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(t: Int) {
                        Toast.makeText(context, "Clear cart success", Toast.LENGTH_SHORT).show()
                        EventBus.getDefault().postSticky(CountCartEvent(true))
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()
                    }
                })
        }

        return super.onOptionsItemSelected(item)
    }

    private fun paymentCash(collectionTime: String, comments: String?) {
        compositeDisposable.add(cartDataSource!!.getAllCart(Common.currentUser!!.uid!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ cartItemList ->
                cartDataSource!!.totalPrice(Common.currentUser!!.uid!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object: SingleObserver<Double> {
                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onSuccess(t: Double) { //
                            // val finalPrice = total_prices!!.toString().toDouble()
                            val order = Order()
//                            val temp = total_prices!!.text.toString().toDouble()
                            order.userId = Common.currentUser!!.uid!!
                            order.userName = Common.currentUser!!.name!!
                            order.userPhone = Common.currentUser!!.phone!!
                            order.collectionTime = collectionTime
                            order.comment = comments
                            order.cartItemList = cartItemList
                            order.totalPayment = total_prices!!.text.toString().toDouble()
                            order.finalPayment = total_prices!!.text.toString().toDouble()
                            order.discount = 0
                            order.isCod = true
                            order.transactionId = "Cash On Delivery"

                            syncLocalTimeWithServerTime(order)

                        }

                        override fun onError(e: Throwable) {
                            Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()
                        }
                    })

            }, { throwable ->
                Toast.makeText(context, "" + throwable.message, Toast.LENGTH_SHORT).show()
            }
        ))
    }

    private fun syncLocalTimeWithServerTime(order: Order) {
        val offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset")
        offsetRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                listener.onLoadTimeFailed(p0.message);
            }

            override fun onDataChange(p0: DataSnapshot) {
                val offset = p0.getValue(Long::class.java)
                val estimatedServerTimeMs = System.currentTimeMillis() + offset!!
                val sdf = SimpleDateFormat("MM dd yyyy, HH:mm")
                val date = Date(estimatedServerTimeMs)
                Log.d("EDMT_DEV", "" + sdf.format(date))
                listener.onLoadTimeSuccess(order, estimatedServerTimeMs)
            }
        })
    }

    private fun submitToFirebase(order: Order) {
        FirebaseDatabase.getInstance()
            .getReference(Common.ORDER_REF)
            .child(Common.orderId())
            .setValue(order)
            .addOnFailureListener { e -> Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()}
            .addOnCompleteListener{ task ->
                if(task.isSuccessful) {
                    cartDataSource!!.cleanCart(Common!!.currentUser!!.uid!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object: SingleObserver<Int> {
                            override fun onSubscribe(d: Disposable) {
                                Toast.makeText(context, "Order placed successfully", Toast.LENGTH_SHORT).show()
                            }

                            override fun onSuccess(t: Int) {
                            }

                            override fun onError(e: Throwable) {
                                Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()
                            }

                        })
                }
            }
    }

    override fun onLoadTimeSuccess(order: Order, estimatedTimeMs: Long) {
        order.createDate = (estimatedTimeMs)
        order.orderStatus = 0;
        submitToFirebase(order)
    }

    override fun onLoadTimeFailed(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}


