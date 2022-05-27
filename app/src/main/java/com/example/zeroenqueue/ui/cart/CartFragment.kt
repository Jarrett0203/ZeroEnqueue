package com.example.zeroenqueue.ui.cart

import android.media.metrics.Event
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.MyCartAdapter
import com.example.zeroenqueue.cart
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.db.CartDataSource
import com.example.zeroenqueue.db.CartDatabase
import com.example.zeroenqueue.db.LocalCartDataSource
import com.example.zeroenqueue.eventBus.HideFABCart
import com.example.zeroenqueue.eventBus.UpdateCartItems
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_cart.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.w3c.dom.Text

class CartFragment: Fragment() {

    private lateinit var cartViewModel: CartViewModel
    private lateinit var compositeDisposable: CompositeDisposable
    private var cartDataSource:CartDataSource?=null
    private var recyclerViewState: Parcelable?=null

    var empty_cart: TextView?=null
    var total_prices: TextView?=null
    var group_place_holder: CardView?=null
    var recycler_cart:RecyclerView?=null

    override fun onResume() {
        super.onResume()
        calculateTotalPrice()
    }

    override fun onCreateView (
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        EventBus.getDefault().postSticky(HideFABCart(true))
        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        cartViewModel.initCartDataSource(requireContext())
        val root = inflater.inflate(R.layout.fragment_cart, container, false)
        initViews(root)
        cartViewModel.getMutableLiveDataCartItems().observe(viewLifecycleOwner, Observer {
            if(it == null || it.isEmpty()) {
                recycler_cart!!.visibility = View.GONE
                group_place_holder!!.visibility = View.GONE
                txt_empty_cart!!.visibility = View.VISIBLE
            } else {
                recycler_cart!!.visibility = View.VISIBLE
                group_place_holder!!.visibility = View.VISIBLE
                txt_empty_cart!!.visibility = View.GONE

                val adapter = MyCartAdapter(requireContext(), it)
                recycler_cart!!.adapter = adapter
            }
        })
        return root
//        val textView:TextView = root.findViewById(R.id.text_tools)
//        toolsViewModel.text.observe(this, Observer {
//            textView.text = it
//        })
    }

    private fun initViews(root:View) {
        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(requireContext()).cartDAO())

        recycler_cart = root.findViewById(R.id.recycler_cart) as RecyclerView
        recycler_cart!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recycler_cart!!.layoutManager = layoutManager
        recycler_cart!!.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))

        empty_cart = root.findViewById(R.id.txt_empty_cart) as TextView
        total_prices = root.findViewById(R.id.txt_total_price) as TextView
        group_place_holder = root.findViewById(R.id.group_place_holder) as CardView
    }

    override fun onStart() {
        super.onStart()
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        cartViewModel!!.onStop()
        compositeDisposable.clear()
        EventBus.getDefault().postSticky(HideFABCart(false))
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onUpdateItemInCart(event: UpdateCartItems) {
        recyclerViewState = recycler_cart!!.layoutManager!!.onSaveInstanceState()
        cartDataSource!!.updateCart(event.cartItem)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Int> {
                override fun onSuccess(t: Int) {
                    calculateTotalPrice();
                    recycler_cart!!.layoutManager!!.onRestoreInstanceState(recyclerViewState)
                }

                override fun onSubscribe(d: Disposable) {
                    TODO("Not yet implemented")
                }


                override fun onError(e: Throwable) {
                    Toast.makeText(context, "[UPDATE CART]" + e.message, Toast.LENGTH_SHORT).show()
                }
            })

    }

    private fun calculateTotalPrice() {
        cartDataSource!!.totalPrice(Common.currentUser!!.uid!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: SingleObserver<Double> {
                override fun onSuccess(t: Double) {
                    txt_total_price!!.text = StringBuilder("Total: ")
                        .append(Common.formatPrice(t))
                    recycler_cart!!.layoutManager!!.onRestoreInstanceState(recyclerViewState)
                }

                override fun onSubscribe(d: Disposable) {
                }


                override fun onError(e: Throwable) {
                    Toast.makeText(context, "[SUM CART]" + e.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

}