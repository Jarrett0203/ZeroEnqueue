package com.example.zeroenqueue.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zeroenqueue.classes.Category
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.classes.Food
import com.example.zeroenqueue.interfaces.IFoodLoadCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CategoryFoodListViewModel : ViewModel(), IFoodLoadCallback {

    private var categoryFoodListMutableLiveData : MutableLiveData<List<Food>>?=null
    private lateinit var messageError:MutableLiveData<String>
    private var categoryFoodCallbackListener: IFoodLoadCallback

    init {
        categoryFoodCallbackListener = this
    }

    val categoryFoodList: LiveData<List<Food>>
        get(){
            if(categoryFoodListMutableLiveData == null){
                categoryFoodListMutableLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadCategoryFood()
            }
            //categoryFoodListMutableLiveData!!.value = Common.categorySelected!!.foods
            return categoryFoodListMutableLiveData!!
        }

    fun loadCategoryFood() {
        val tempList = ArrayList<Food>()
        val categoryFoodRef = FirebaseDatabase.getInstance(Common.DATABASE_LINK).getReference(Common.FOODLIST_REF)
        categoryFoodRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(itemSnapShot in snapshot.children){
                    val categoryFood = itemSnapShot.getValue(Food::class.java)
                    if (categoryFood!!.categories == Common.categorySelected!!.name) {
                        tempList.add(categoryFood)
                    }
                }
                categoryFoodCallbackListener.onFoodLoadSuccess(tempList)
            }

            override fun onCancelled(error: DatabaseError) {
                categoryFoodCallbackListener.onFoodLoadFailed(error.message)
            }
        })
    }

    override fun onFoodLoadSuccess(foodList: List<Food>) {
        categoryFoodListMutableLiveData!!.value = foodList
    }

    override fun onFoodLoadFailed(message: String) {
        messageError.value = message
    }

    fun getCategoryFoodList(): MutableLiveData<List<Food>> {
        if(categoryFoodListMutableLiveData == null){
            categoryFoodListMutableLiveData = MutableLiveData()
            loadCategoryFood()
        }
        return categoryFoodListMutableLiveData!!
    }

}