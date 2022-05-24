package com.example.zeroenqueue.interfaces

import com.example.zeroenqueue.classes.User


interface IProfileLoadCallback {
    fun onProfileLoadSuccess(profile: User)
    fun onProfileLoadFailed(message:String)
}