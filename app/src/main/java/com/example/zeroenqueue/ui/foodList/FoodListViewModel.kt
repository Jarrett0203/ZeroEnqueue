package com.example.zeroenqueue.ui.foodList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zeroenqueue.classes.Food
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.interfaces.IFoodLoadCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FoodListViewModel : ViewModel(), IFoodLoadCallback {
    private var foodListMutableLiveData: MutableLiveData<List<Food>> ?= null
    private lateinit var messageError:MutableLiveData<String>
    private var foodCallbackListener : IFoodLoadCallback

    init {
        foodCallbackListener = this
    }

    val foodList:LiveData<List<Food>>
        get(){
            if(foodListMutableLiveData == null){
                foodListMutableLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadFoodList()
            }
            return foodListMutableLiveData!!
        }

    fun getFoodList(): MutableLiveData<List<Food>> {
        if(foodListMutableLiveData == null){
            foodListMutableLiveData = MutableLiveData()
            loadFoodList()
        }
        return foodListMutableLiveData!!
    }

    fun loadFoodList() {
        val tempList = ArrayList<Food>()
        val foodListRef = FirebaseDatabase.getInstance(Common.DATABASE_LINK).getReference(Common.FOODLIST_REF)
        foodListRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(itemSnapShot in snapshot.children){
                    val food = itemSnapShot.getValue(Food::class.java)
                    tempList.add(food!!)
                }
                foodCallbackListener.onFoodLoadSuccess(tempList)
            }

            override fun onCancelled(error: DatabaseError) {
                foodCallbackListener.onFoodLoadFailed(error.message)
            }
        })
    }

    override fun onFoodLoadSuccess(categoryList: List<Food>) {
        foodListMutableLiveData!!.value = categoryList
    }

    override fun onFoodLoadFailed(message: String) {
        messageError.value = message
    }


}