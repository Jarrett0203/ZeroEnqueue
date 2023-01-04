package com.example.zeroenqueue.uiVendor.menu

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

class MenuViewModel : ViewModel(), IFoodLoadCallback {
    private var menuListMutableLiveData: MutableLiveData<List<Food>> ?= null
    private lateinit var messageError:MutableLiveData<String>
    private var foodCallbackListener : IFoodLoadCallback = this

    val menuList:LiveData<List<Food>>
        get(){
            if(menuListMutableLiveData == null){
                menuListMutableLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadMenuList(Common.rating)
            }
            return menuListMutableLiveData!!
        }

    fun loadMenuList(rating: Double) {
        val tempList = ArrayList<Food>()
        val foodListRef = FirebaseDatabase.getInstance(Common.DATABASE_LINK).getReference(Common.FOODLIST_REF)
        foodListRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemSnapShot in snapshot.children) {
                    val food = itemSnapShot.getValue(Food::class.java)
                    if ((food!!.ratingValue/ food.ratingCount) >= rating && food.foodStall == Common.foodStallSelected!!.id)
                        tempList.add(food)
                    foodCallbackListener.onFoodLoadSuccess(tempList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                foodCallbackListener.onFoodLoadFailed(error.message)
            }
        })
    }

    override fun onFoodLoadSuccess(foodList: List<Food>) {
        menuListMutableLiveData!!.value = foodList
    }

    override fun onFoodLoadFailed(message: String) {
        messageError.value = message
    }
}