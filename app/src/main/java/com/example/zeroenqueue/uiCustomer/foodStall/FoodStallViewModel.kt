package com.example.zeroenqueue.uiCustomer.foodStall

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zeroenqueue.classes.FoodStall
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.interfaces.IFoodStallLoadCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FoodStallViewModel : ViewModel(), IFoodStallLoadCallback {

    private var foodStallListMutableLiveData: MutableLiveData<List<FoodStall>>? = null
    private var messageError: MutableLiveData<kotlin.String>? = null
    private var foodStallCallbackListener: IFoodStallLoadCallback

    init {
        foodStallCallbackListener = this
    }

    val foodStallList: LiveData<List<FoodStall>>
        get() {
            if (foodStallListMutableLiveData == null) {
                foodStallListMutableLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadFoodStall()
            }
            return foodStallListMutableLiveData!!
        }

    val errorMessage: LiveData<kotlin.String>
        get() {
            if (messageError == null) {
                messageError = MutableLiveData()
                loadFoodStall()
            }
            return messageError!!
        }

    private fun loadFoodStall() {
        val tempList = ArrayList<FoodStall>()
        val foodStallRef =
            FirebaseDatabase.getInstance(Common.DATABASE_LINK).getReference(Common.FOODSTALL_REF)
        foodStallRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemSnapShot in snapshot.children) {
                    val foodStall = itemSnapShot.getValue(FoodStall::class.java)
                    foodStall!!.id = itemSnapShot.key
                    tempList.add(foodStall)
                }
                foodStallCallbackListener.onFoodStallLoadSuccess(tempList)
            }

            override fun onCancelled(error: DatabaseError) {
                foodStallCallbackListener.onFoodStallLoadFailed(error.message)
            }
        })
    }

    override fun onFoodStallLoadSuccess(foodStallList: List<FoodStall>) {
        foodStallListMutableLiveData!!.value = foodStallList
    }

    override fun onFoodStallLoadFailed(message: kotlin.String) {
        messageError!!.value = message
    }

}