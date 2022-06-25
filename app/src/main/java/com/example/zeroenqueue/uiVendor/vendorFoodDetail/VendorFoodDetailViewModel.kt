package com.example.zeroenqueue.uiVendor.vendorFoodDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import com.example.zeroenqueue.classes.Comment
import com.example.zeroenqueue.classes.Food
import com.example.zeroenqueue.common.Common

class VendorFoodDetailViewModel : ViewModel() {
    private var foodDetailMutableLiveData: MutableLiveData<Food>? = null
    private var commentMutableLiveData: MutableLiveData<Comment>?=null

    val foodDetail: LiveData<Food>
        get(){
            if(foodDetailMutableLiveData == null){
                foodDetailMutableLiveData = MutableLiveData()
            }
            foodDetailMutableLiveData!!.value = Common.foodSelected
            return foodDetailMutableLiveData!!.distinctUntilChanged()
        }

    val comment:LiveData<Comment>
        get(){
            if(commentMutableLiveData == null){
                commentMutableLiveData = MutableLiveData()
            }
            return commentMutableLiveData!!
        }
}