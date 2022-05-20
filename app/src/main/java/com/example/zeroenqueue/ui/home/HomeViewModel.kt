package com.example.zeroenqueue.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zeroenqueue.callback.IPopularLoadCallback
import com.example.zeroenqueue.callback.IRecommendedLoadCallback
import com.example.zeroenqueue.model.PopularCategory
import com.example.zeroenqueue.model.Recommended
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeViewModel : ViewModel(), IPopularLoadCallback, IRecommendedLoadCallback {

    private var popularListMutableLiveData: MutableLiveData<List<PopularCategory>> ?= null
    private var recommendedListMutableLiveData: MutableLiveData<List<Recommended>> ?= null
    private lateinit var messageError:MutableLiveData<String>
    private var popularLoadCallbackListener : IPopularLoadCallback
    private var recommendedCallbackListener : IRecommendedLoadCallback

    val popularList:LiveData<List<PopularCategory>>
        get(){
            if(popularListMutableLiveData == null){
                popularListMutableLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadPopularList()
            }
            return popularListMutableLiveData!!
        }

    val recommendedList:LiveData<List<Recommended>>
        get(){
            if(recommendedListMutableLiveData == null){
                recommendedListMutableLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadRecommendedList()
            }
            return recommendedListMutableLiveData!!
        }

    private fun loadPopularList() {
        val tempList = ArrayList<PopularCategory>()
        val popularRef = FirebaseDatabase.getInstance("https://zeroenqueue-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("MostPopular")
        popularRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(itemSnapShot in snapshot.children){
                    val model = itemSnapShot.getValue(PopularCategory::class.java)
                    tempList.add(model!!)
                }
                popularLoadCallbackListener.onPopularLoadSuccess(tempList)
            }

            override fun onCancelled(error: DatabaseError) {
                popularLoadCallbackListener.onPopularLoadFailed(error.message)
            }

        })
    }

    private fun loadRecommendedList() {
        val tempList = ArrayList<Recommended>()
        val recommendedRef = FirebaseDatabase.getInstance("https://zeroenqueue-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Recommended")
        recommendedRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(itemSnapShot in snapshot.children){
                    val model = itemSnapShot.getValue(Recommended::class.java)
                    tempList.add(model!!)
                }
                recommendedCallbackListener.onRecommendedLoadSuccess(tempList)
            }

            override fun onCancelled(error: DatabaseError) {
                recommendedCallbackListener.onRecommendedLoadFailed(error.message)
            }

        })
    }

    init {
        popularLoadCallbackListener = this
        recommendedCallbackListener = this
    }

    override fun onPopularLoadSuccess(popularList: List<PopularCategory>) {
        popularListMutableLiveData!!.value = popularList
    }

    override fun onPopularLoadFailed(message: String) {
        messageError.value = message
    }

    override fun onRecommendedLoadSuccess(recommendedList: List<Recommended>) {
        recommendedListMutableLiveData!!.value = recommendedList
    }

    override fun onRecommendedLoadFailed(message: String) {
        messageError.value = message
    }

    /*private val _foodItems = MutableLiveData<List<FoodItem>>().apply {
        value = listOf(
            FoodItem(1, "https://i1.lensdump.com/i/tJ76mZ.png", 5.50, 78, 132123, "Fish and Chips",
                "The Tea Party", "Western", true),
            FoodItem(2, "https://i1.lensdump.com/i/tJ76mZ.png", 5.50,78, 132123, "Fish and Chips",
                "The Tea Party", "Western", false),
            FoodItem(3, "https://i1.lensdump.com/i/tJ76mZ.png",5.50, 78, 132123, "Fish and Chips",
                "The Tea Party", "Western", false)
        )
    }
    val foodItems: LiveData<List<FoodItem>> =_foodItems*/
}