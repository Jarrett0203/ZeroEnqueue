package com.example.zeroenqueue.uiVendor.vendorFoodDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zeroenqueue.classes.Food
import com.example.zeroenqueue.common.Common

class VendorFoodDetailViewModel : ViewModel() {
    private var foodDetailMutableLiveData: MutableLiveData<Food>? = null
    val foodDetail: LiveData<Food>
        get(){
            if(foodDetailMutableLiveData == null){
                foodDetailMutableLiveData = MutableLiveData()
            }
            foodDetailMutableLiveData!!.value = Common.foodSelected
            return foodDetailMutableLiveData!!
        }
    fun setFood(food: Food) {
        if(foodDetailMutableLiveData != null){
            foodDetailMutableLiveData!!.value = food
        }
    }
}