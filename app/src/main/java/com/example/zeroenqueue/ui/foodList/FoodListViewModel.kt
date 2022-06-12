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

    val foodListWithCategory:LiveData<List<Food>>
        get(){
            if(foodListMutableLiveData == null){
                foodListMutableLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadFoodListWithCategory(arrayListOf(Common.categorySelected!!.name!!))
            }
            return foodListMutableLiveData!!
        }

    val foodListWithFoodStall:LiveData<List<Food>>
        get(){
            if(foodListMutableLiveData == null){
                foodListMutableLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadFoodListWithFoodStall(arrayListOf(Common.foodStallSelected!!.name!!))
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

    fun loadFoodListSearch(search: String) {
        val tempList = ArrayList<Food>()
        val foodListRef = FirebaseDatabase.getInstance(Common.DATABASE_LINK).getReference(Common.FOODLIST_REF)
        foodListRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(itemSnapShot in snapshot.children){
                    val searchFood = itemSnapShot.getValue(Food::class.java)
                    if (searchFood!!.name!!.lowercase().contains(search))
                        tempList.add(searchFood)
                }
                foodCallbackListener.onFoodLoadSuccess(tempList)
            }

            override fun onCancelled(error: DatabaseError) {
                foodCallbackListener.onFoodLoadFailed(error.message)
            }
        })
    }

    fun loadFoodListWithCategory(categoryList: List<String>) {
        val tempList = ArrayList<Food>()
        val foodListRef = FirebaseDatabase.getInstance(Common.DATABASE_LINK).getReference(Common.FOODLIST_REF)
        foodListRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(itemSnapShot in snapshot.children){
                    val categoryFood = itemSnapShot.getValue(Food::class.java)
                    categoryList.forEach{ s ->
                        if (categoryFood!!.categories!!.uppercase() == s!!.uppercase())
                            tempList.add(categoryFood)
                    }
                }
                foodCallbackListener.onFoodLoadSuccess(tempList)
            }

            override fun onCancelled(error: DatabaseError) {
                foodCallbackListener.onFoodLoadFailed(error.message)
            }
        })
    }

    fun loadFoodListWithFoodStall(foodStallList: List<String>) {
        val tempList = ArrayList<Food>()
        val foodListRef = FirebaseDatabase.getInstance(Common.DATABASE_LINK).getReference(Common.FOODLIST_REF)
        foodListRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemSnapShot in snapshot.children) {
                    val food = itemSnapShot.getValue(Food::class.java)
                    foodStallList.forEach { s ->
                        if (food!!.foodStall!!.uppercase() == s.uppercase())
                            tempList.add(food)
                    }
                    foodCallbackListener.onFoodLoadSuccess(tempList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                foodCallbackListener.onFoodLoadFailed(error.message)
            }
        })
    }

    fun loadFoodListWithFoodStallAndCategories(foodStallList: List<String>, categoryList: List<String>) {
        val tempList = ArrayList<Food>()
        val foodListRef = FirebaseDatabase.getInstance(Common.DATABASE_LINK).getReference(Common.FOODLIST_REF)
        foodListRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemSnapShot in snapshot.children) {
                    val food = itemSnapShot.getValue(Food::class.java)
                    foodStallList.forEach { s ->
                        if (food!!.foodStall!!.uppercase() == s.uppercase())
                            tempList.add(food)
                    }
                    categoryList.forEach { s ->
                        if (food!!.categories!!.uppercase() == s.uppercase()) {
                            if (!tempList.contains(food))
                                tempList.add(food)
                        }
                    }
                    foodCallbackListener.onFoodLoadSuccess(tempList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                foodCallbackListener.onFoodLoadFailed(error.message)
            }
        })
    }

    override fun onFoodLoadSuccess(foodList: List<Food>) {
        foodListMutableLiveData!!.value = foodList
    }

    override fun onFoodLoadFailed(message: String) {
        messageError.value = message
    }


}