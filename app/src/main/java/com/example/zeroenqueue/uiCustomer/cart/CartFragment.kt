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
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zeroenqueue.Notifications.IFCMService
import com.example.zeroenqueue.Notifications.RetrofitFCMClient
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.MyCartAdapter
import com.example.zeroenqueue.classes.FCMResponse
import com.example.zeroenqueue.classes.FCMSendData
import com.example.zeroenqueue.classes.Order
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.common.SwipeHelper
import com.example.zeroenqueue.databinding.FragmentCartBinding
import com.example.zeroenqueue.db.CartDataSource
import com.example.zeroenqueue.db.CartDatabase
import com.example.zeroenqueue.db.CartItem
import com.example.zeroenqueue.db.LocalCartDataSource
import com.example.zeroenqueue.eventBus.CountCartEvent
import com.example.zeroenqueue.eventBus.HideFABCart
import com.example.zeroenqueue.eventBus.UpdateCartItems
import com.example.zeroenqueue.interfaces.IDeleteBtnCallback
import com.example.zeroenqueue.interfaces.ILoadTimeFromFirebaseCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class CartFragment: Fragment(), ILoadTimeFromFirebaseCallback {

    private var _binding: FragmentCartBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var cartViewModel: CartViewModel
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var cartDataSource:CartDataSource?=null
    private var recyclerViewState: Parcelable?=null
    private lateinit var btn_place_order: Button
    private lateinit var empty_cart: TextView
    private lateinit var total_prices: TextView
    private lateinit var group_place_holder: CardView
    private lateinit var recycler_cart:RecyclerView
    private lateinit var adapter: MyCartAdapter
    private lateinit var comments: TextView


    lateinit var ifcmService: IFCMService
    lateinit var listener: ILoadTimeFromFirebaseCallback

    override fun onResume() {
        super.onResume()
        calculateTotalPrice()
    }

    override fun onCreateView (
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)

        EventBus.getDefault().postSticky(HideFABCart(true))
        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]
        cartViewModel.initCartDataSource(requireContext())
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val navController = Navigation.findNavController(activity!!, R.id.nav_host_fragment_content_main)

        recycler_cart = binding.recyclerCart
        recycler_cart.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recycler_cart.layoutManager = layoutManager
        recycler_cart.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))

        empty_cart = binding.txtEmptyCart
        total_prices = binding.txtTotalPrice
        group_place_holder = binding.groupPlaceHolder
        val btn_use_discounts = binding.btnUseDiscounts
        btn_place_order = binding.btnPlaceOrder

        btn_place_order.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("One more step!")

            val view = LayoutInflater.from(context).inflate(R.layout.layout_place_order, null)
            val collectionTime = view.findViewById<View>(R.id.time) as EditText
            val cash = view.findViewById<View>(R.id.cash) as RadioButton
            //val braintree = view.findViewById<View>(R.id.braintree) as RadioButton

            //collectionTime.setText(Common.currentUser!!.address)

            builder.setView(view)
            builder.setNegativeButton("No") { dialogInterface, _ -> dialogInterface.dismiss() }
                .setPositiveButton("YES") { _, _ ->
                    if (cash.isChecked)
                        paymentCash(collectionTime.text.toString(), comments.text.toString())
                }

            val dialog = builder.create()
            dialog.show()

        }

        initViews()

        btn_use_discounts.setOnClickListener {
            if (Common.cartItemSelected == null)
                Toast.makeText(requireContext(), "Please select a food item", Toast.LENGTH_SHORT).show()
            else {
                if (Common.cartItemSelected!!.discount != 0.0)
                    Toast.makeText(requireContext(), "Discount has already been redeemed!", Toast.LENGTH_SHORT).show()
                else
                    navController.navigate(R.id.navigation_useDiscounts)
            }
        }

        cartViewModel.getMutableLiveDataCartItems().observe(viewLifecycleOwner) {
            if (it == null || it.isEmpty()) {
                recycler_cart.visibility = View.GONE
                group_place_holder.visibility = View.GONE
                empty_cart.visibility = View.VISIBLE
            } else {
                recycler_cart.visibility = View.VISIBLE
                group_place_holder.visibility = View.VISIBLE
                empty_cart.visibility = View.GONE

                adapter = MyCartAdapter(requireContext(), it)
                recycler_cart.adapter = adapter
            }
        }
        return root
    }

    private fun initViews() {

        //setHasOptionsMenu(true)

        ifcmService = RetrofitFCMClient.getInstance().create(IFCMService::class.java)

        val rootComment = layoutInflater.inflate(R.layout.layout_rating_comment, null)

        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(requireContext()).cartDAO())

        listener = this
        comments = rootComment.findViewById(R.id.edit_comment) as TextView

        val swipe = object : SwipeHelper(requireContext(), recycler_cart, 200) {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>
            ) {
                buffer.add(MyButton(requireContext(),
                    "Delete",
                    30,
                    0,
                    Color.parseColor("#FF3c30"),
                    object: IDeleteBtnCallback {
                        override fun onClick(pos: Int) {
                            val itemDelete = adapter.getItemAtPosition(pos)
                            cartDataSource!!.deleteCart(itemDelete)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object:SingleObserver<Int> {
                                    override fun onSubscribe(d: Disposable) {

                                    }

                                    override fun onSuccess(t: Int) {
                                        adapter.notifyItemRemoved(pos)
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
    }

    private fun calculateTotalPrice() {
        cartDataSource!!.totalPrice(Common.currentUser!!.uid!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Double> {
                override fun onSuccess(t: Double) {
                    total_prices.text = Common.formatPrice(t)
                    recycler_cart.layoutManager!!.onRestoreInstanceState(recyclerViewState)
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
        recyclerViewState = recycler_cart.layoutManager!!.onSaveInstanceState()
        cartDataSource!!.updateCart(event.cartItem)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Int> {
                override fun onSuccess(t: Int) {
                    calculateTotalPrice()
                    recycler_cart.layoutManager!!.onRestoreInstanceState(recyclerViewState)
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(context, "[UPDATE CART]" + e.message, Toast.LENGTH_SHORT).show()
                }
            })

    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.cart_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_settings).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_clear_cart) {
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
                val hashMap = HashMap<String, ArrayList<CartItem>>()
                for (item in cartItemList) {
                    if (hashMap.containsKey(item.foodStall)) {
                        val existingArrayList = hashMap[item.foodStall]
                        existingArrayList!!.add(item)
                        hashMap.replace(item.foodStall!!, existingArrayList)
                    }
                    else {
                        val newArrayList = ArrayList<CartItem>()
                        newArrayList.add(item)
                        hashMap[item.foodStall!!] = newArrayList
                    }
                }

                hashMap.forEach { (foodStallId, cartItemList) ->
                    cartDataSource!!.foodStallTotalPrice(Common.currentUser!!.uid!!, foodStallId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object: SingleObserver<Double> {
                            override fun onSubscribe(d: Disposable) {

                            }

                            override fun onSuccess(t: Double) {
                                val order = Order()
                                order.userId = Common.currentUser!!.uid!!
                                order.userName = Common.currentUser!!.name!!
                                order.userPhone = Common.currentUser!!.phone!!
                                order.collectionTime = collectionTime
                                order.comment = comments
                                order.cartItemList = cartItemList
                                order.totalPayment = t
                                order.finalPayment = t
                                order.discount = 0
                                order.isCod = true
                                order.transactionId = "Cash On Delivery"
                                order.foodStallId = foodStallId

                                syncLocalTimeWithServerTime(order)

                            }

                            override fun onError(e: Throwable) {
                                if (!e.message!!.contains("Query returned empty"))
                                    Toast.makeText(context, "[SUM CART" + e.message, Toast.LENGTH_SHORT).show()
                            }
                        })
                }
            }, { throwable ->
                Toast.makeText(context, "" + throwable.message, Toast.LENGTH_SHORT).show()
            }
            ))
    }

    private fun syncLocalTimeWithServerTime(order: Order) {
        val offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset")
        offsetRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                listener.onLoadTimeFailed(p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                val offset = p0.getValue(Long::class.java)
                val estimatedServerTimeMs = System.currentTimeMillis() + offset!!
                val sdf = SimpleDateFormat("MM dd yyyy, HH:mm")
                val date = Date(estimatedServerTimeMs)
                Log.d("ZE", "" + sdf.format(date))
                listener.onLoadTimeSuccess(order, estimatedServerTimeMs)
            }
        })
    }

    override fun onLoadTimeSuccess(order: Order, estimatedTimeMs: Long) {
        order.createDate = (estimatedTimeMs)
        order.orderStatus = 0
        submitToFirebase(order)
    }

    override fun onLoadTimeFailed(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun submitToFirebase(order: Order) {
        FirebaseDatabase.getInstance()
            .getReference(Common.ORDER_REF)
            .child(Common.randomTimeId())
            .setValue(order)
            .addOnFailureListener { e -> Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()}
            .addOnCompleteListener{ task ->
                if(task.isSuccessful) {
                    cartDataSource!!.cleanCart(Common.currentUser!!.uid!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object: SingleObserver<Int> {
                            override fun onSubscribe(d: Disposable) {


                            }

                            override fun onSuccess(t: Int) {
                                val dataSend = HashMap<String, String>()
                                dataSend.put(Common.NOTI_TITLE, "New Order")
                                dataSend.put(Common.NOTI_CONTENT, "You have a new order" + Common.currentUser!!.phone)

                                val sendData = FCMSendData(Common.getNewOrderTopic(), dataSend)

                                compositeDisposable.add(
                                    ifcmService.sendNotification(sendData)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe ({
                                             t: FCMResponse ->
                                                if(t.success != 0)
                                                    Toast.makeText(context!!, "Order placed successfully", Toast.LENGTH_SHORT).show()
                                        }, {t: Throwable? ->
                                            Toast.makeText(context!!, "Order was sent but notification system failed", Toast.LENGTH_SHORT).show()
                                        })
                                )

                                Toast.makeText(context, "Order placed successfully", Toast.LENGTH_SHORT).show()
                            }

                            override fun onError(e: Throwable) {
                                Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()
                            }

                        })
                }
            }
    }

}


