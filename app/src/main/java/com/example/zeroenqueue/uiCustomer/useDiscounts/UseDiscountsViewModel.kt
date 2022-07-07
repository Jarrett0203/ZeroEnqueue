package com.example.zeroenqueue.uiCustomer.useDiscounts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zeroenqueue.classes.Discount
import com.example.zeroenqueue.classes.User
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.interfaces.IDiscountLoadCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UseDiscountsViewModel : ViewModel(), IDiscountLoadCallback {
    private var discountListMutableLiveData: MutableLiveData<List<Discount>>? = null
    private lateinit var messageError: MutableLiveData<String>
    private var discountCallbackListener: IDiscountLoadCallback = this

    val discountList: LiveData<List<Discount>>
        get() {
            if (discountListMutableLiveData == null) {
                discountListMutableLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadDiscountsList()
            }
            return discountListMutableLiveData!!
        }

    fun loadDiscountsList() {
        val tempList = ArrayList<Discount>()
        val userRef =
            FirebaseDatabase.getInstance(Common.DATABASE_LINK).getReference(Common.USER_REF).child(Common.currentUser!!.uid!!)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user!!.redeemedDiscounts!!.isNotEmpty()) {
                    for (discount in user.redeemedDiscounts!!) {
                        if (discount.foodUid == Common.cartItemSelected!!.foodId && !discount.isExpired && !discount.isRedeemed)
                            tempList.add(discount)
                    }
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