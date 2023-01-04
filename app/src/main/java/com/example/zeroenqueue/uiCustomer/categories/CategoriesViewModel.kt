package com.example.zeroenqueue.uiCustomer.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.interfaces.ICategoryLoadCallback
import com.example.zeroenqueue.classes.Category
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CategoriesViewModel : ViewModel(), ICategoryLoadCallback {

    private var categoryListMutableLiveData: MutableLiveData<List<Category>> ?= null
    private var messageError:MutableLiveData<String> ?= null
    private var categoryCallbackListener : ICategoryLoadCallback = this

    val categoryList:LiveData<List<Category>>
        get(){
            if(categoryListMutableLiveData == null){
                categoryListMutableLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadCategory()
            }
            return categoryListMutableLiveData!!
        }

    val errorMessage:LiveData<String>
        get() {
            if(messageError == null){
                messageError = MutableLiveData()
                loadCategory()
            }
            return messageError!!
        }

    private fun loadCategory() {
        val tempList = ArrayList<Category>()
        val categoryRef = FirebaseDatabase.getInstance(Common.DATABASE_LINK).getReference(Common.CATEGORY_REF)
        categoryRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(itemSnapShot in snapshot.children){
                    val category = itemSnapShot.getValue(Category::class.java)
                    category!!.menu_id = itemSnapShot.key
                    tempList.add(category)
                }
                categoryCallbackListener.onCategoryLoadSuccess(tempList)
            }

            override fun onCancelled(error: DatabaseError) {
                categoryCallbackListener.onCategoryLoadFailed(error.message)
            }
        })
    }

    override fun onCategoryLoadSuccess(categoryList: List<Category>) {
        categoryListMutableLiveData!!.value = categoryList
    }

    override fun onCategoryLoadFailed(message: String) {
        messageError!!.value = message
    }

}