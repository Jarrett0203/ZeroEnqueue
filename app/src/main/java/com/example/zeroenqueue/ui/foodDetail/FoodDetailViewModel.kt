package com.example.zeroenqueue.ui.foodDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.model.Food

class FoodDetailViewModel : ViewModel() {

    private var foodDetailMutableLiveData: MutableLiveData<Food>?=null

    val foodDetail:LiveData<Food>
        get(){
            if(foodDetailMutableLiveData == null){
                foodDetailMutableLiveData = MutableLiveData()
            }
            foodDetailMutableLiveData!!.value = Common.foodSelected
            return foodDetailMutableLiveData!!
        }
}