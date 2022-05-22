package com.example.zeroenqueue.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.classes.Food


class CategoryFoodListViewModel : ViewModel() {

    private var categoryFoodListMutableLiveData : MutableLiveData<List<Food>>?=null
    private var messageError:MutableLiveData<String> ?= null

    val categoryFoodList: LiveData<List<Food>>
        get(){
            if(categoryFoodListMutableLiveData == null){
                categoryFoodListMutableLiveData = MutableLiveData()
                messageError = MutableLiveData()
            }
            categoryFoodListMutableLiveData!!.value = Common.categorySelected!!.foods
            return categoryFoodListMutableLiveData!!
        }

}