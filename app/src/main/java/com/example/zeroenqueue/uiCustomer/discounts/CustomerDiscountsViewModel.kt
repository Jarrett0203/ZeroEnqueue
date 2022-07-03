package com.example.zeroenqueue.uiCustomer.discounts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zeroenqueue.classes.Discount
import com.example.zeroenqueue.classes.Food
import com.example.zeroenqueue.classes.User
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.interfaces.IDiscountLoadCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CustomerDiscountsViewModel : ViewModel(), IDiscountLoadCallback {
    private var discountListMutableLiveData: MutableLiveData<List<Discount>>? = null
    private lateinit var messageError: MutableLiveData<String>
    private var discountCallbackListener: IDiscountLoadCallback = this

    val discountList: LiveData<List<Discount>>
        get() {
            if (discountListMutableLiveData == null) {
                discountListMutableLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadDiscountList()
            }
            return discountListMutableLiveData!!
        }

    fun loadDiscountList() {
        val tempList = ArrayList<Discount>()
        val userRef =
            FirebaseDatabase.getInstance(Common.DATABASE_LINK).getReference(Common.DISCOUNT_REF)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemSnapShot in snapshot.children) {
                    val discount = itemSnapShot.getValue(Discount::class.java)
                        tempList.add(discount!!)
                }
                discountCallbackListener.onDiscountLoadSuccess(tempList)
            }

            override fun onCancelled(error: DatabaseError) {
                discountCallbackListener.onDiscountLoadFailed(error.message)
            }
        })
    }

    override fun onDiscountLoadSuccess(discountList: List<Discount>) {
        discountListMutableLiveData!!.value = discountList
    }

    override fun onDiscountLoadFailed(message: String) {
        messageError.value = message
    }
}