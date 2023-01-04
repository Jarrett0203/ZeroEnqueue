package com.example.zeroenqueue.interfaces

import com.example.zeroenqueue.classes.Message

interface IMessageCallback {
    fun onMessageLoadSuccess(messageList:ArrayList<Message>)
    fun onMessageLoadFailed(message:String)
}
