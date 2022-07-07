package com.example.zeroenqueue.uiCustomer.foodDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zeroenqueue.classes.Comment
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.classes.Food

class FoodDetailViewModel : ViewModel() {

    private var foodDetailMutableLiveData: MutableLiveData<Food>? = null
    private var commentMutableLiveData: MutableLiveData<Comment>? = null

    val foodDetail: LiveData<Food>
        get() {
            if (foodDetailMutableLiveData == null) {
                foodDetailMutableLiveData = MutableLiveData()
            }
            foodDetailMutableLiveData!!.value = Common.foodSelected
            return foodDetailMutableLiveData!!
        }

    val comment: LiveData<Comment>
        get() {
            if (commentMutableLiveData == null) {
                commentMutableLiveData = MutableLiveData()
            }
            return commentMutableLiveData!!
        }

    fun setComment(comment: Comment) {
        if (commentMutableLiveData != null) {
            commentMutableLiveData!!.value = comment
        }
    }

    fun setFood(food: Food) {
        if (foodDetailMutableLiveData != null) {
            foodDetailMutableLiveData!!.value = food
        }
    }
}