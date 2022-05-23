package com.example.zeroenqueue.ui.foodStall

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zeroenqueue.classes.Food
import com.example.zeroenqueue.classes.FoodStall
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.interfaces.IFoodLoadCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FoodStallMenuViewModel : ViewModel(), IFoodLoadCallback {

    private var foodStallMenuMutableLiveData : MutableLiveData<List<Food>>?=null
    private lateinit var messageError: MutableLiveData<String>
    private var foodStallCallbackListener: IFoodLoadCallback

    init {
        foodStallCallbackListener = this
    }

    val foodList: LiveData<List<Food>>
    get(){
        if(foodStallMenuMutableLiveData == null){
            foodStallMenuMutableLiveData = MutableLiveData()
            messageError = MutableLiveData()
            loadFoodStallMenu()
        }
        return foodStallMenuMutableLiveData!!
    }

    fun loadFoodStallMenu() {
        val tempList = ArrayList<Food>()
        val foodRef = FirebaseDatabase.getInstance(Common.DATABASE_LINK).getReference(Common.FOODLIST_REF)
        foodRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(itemSnapShot in snapshot.children){
                    val stallMenuFood = itemSnapShot.getValue(Food::class.java)
                    if (stallMenuFood!!.foodStall == Common.foodStallSelected!!.name) {
                        tempList.add(stallMenuFood)
                    }
                }
                foodStallCallbackListener.onFoodLoadSuccess(tempList)
            }

            override fun onCancelled(error: DatabaseError) {
                foodStallCallbackListener.onFoodLoadFailed(error.message)
            }
        })
    }

    override fun onFoodLoadSuccess(foodList: List<Food>) {
        foodStallMenuMutableLiveData!!.value = foodList
    }

    override fun onFoodLoadFailed(message: String) {
        messageError.value = message
    }

    fun getCategoryFoodList(): MutableLiveData<List<Food>> {
        if(foodStallMenuMutableLiveData == null){
            foodStallMenuMutableLiveData = MutableLiveData()
            loadFoodStallMenu()
        }
        return foodStallMenuMutableLiveData!!
    }


}