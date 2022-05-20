package com.example.zeroenqueue.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zeroenqueue.callback.ICategoryLoadCallback
import com.example.zeroenqueue.callback.IPopularLoadCallback
import com.example.zeroenqueue.model.Category
import com.example.zeroenqueue.model.PopularCategory
import com.example.zeroenqueue.model.Recommended
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CategoriesViewModel : ViewModel(), ICategoryLoadCallback {

    private var categoryListMutableLiveData: MutableLiveData<List<Category>> ?= null
    private var messageError:MutableLiveData<String> ?= null
    private var categoryCallbackListener : ICategoryLoadCallback

    init {
        categoryCallbackListener = this
    }

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
        val categoryRef = FirebaseDatabase.getInstance("https://zeroenqueue-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Category")
        categoryRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(itemSnapShot in snapshot.children){
                    val model = itemSnapShot.getValue(Category::class.java)
                    model!!.menu_id = itemSnapShot.key
                    tempList.add(model)
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