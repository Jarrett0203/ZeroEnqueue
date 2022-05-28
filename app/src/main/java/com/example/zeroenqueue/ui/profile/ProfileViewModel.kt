package com.example.zeroenqueue.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zeroenqueue.classes.Food
import com.example.zeroenqueue.classes.FoodStall
import com.example.zeroenqueue.classes.User
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.interfaces.IFoodStallLoadCallback
import com.example.zeroenqueue.interfaces.IProfileLoadCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileViewModel : ViewModel(), IProfileLoadCallback {
    private var profileMutableLiveData: MutableLiveData<User>? = null
    private lateinit var messageError: MutableLiveData<String>
    private var profileCallbackListener: IProfileLoadCallback

    init {
        profileCallbackListener = this
    }

    val profile: LiveData<User>
        get() {
            if (profileMutableLiveData == null) {
                profileMutableLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadProfile()
            }
            return profileMutableLiveData!!
        }

    private fun loadProfile() {
        val userRef =
            FirebaseDatabase.getInstance(Common.DATABASE_LINK).getReference(Common.USER_REF)
        var profile: User? = null
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemSnapShot in snapshot.children) {
                    val user = itemSnapShot.getValue(User::class.java)
                    if (user!!.uid == Common.currentUser!!.uid) {
                        profile = user
                        break
                    }
                }
                profileCallbackListener.onProfileLoadSuccess(profile!!)
            }

            override fun onCancelled(error: DatabaseError) {
                profileCallbackListener.onProfileLoadFailed(error.message)
            }
        })
    }

    fun setProfile(profile: User) {
        if (profileMutableLiveData != null) {
            profileMutableLiveData!!.value = profile
        }
    }

    override fun onProfileLoadSuccess(profile: User) {
        profileMutableLiveData!!.value = profile
    }

    override fun onProfileLoadFailed(message: String) {
        messageError.value = message
    }
}