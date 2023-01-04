package com.example.zeroenqueue.uiGeneral.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zeroenqueue.classes.Message

class ChatViewModel : ViewModel() {
    private var messageListMutableLiveData: MutableLiveData<ArrayList<Message>>? = MutableLiveData()

    val messageList: LiveData<ArrayList<Message>>
        get() {
            return messageListMutableLiveData!!
        }

    fun setMessageList(messageList: ArrayList<Message>){
        messageListMutableLiveData!!.value = messageList
    }
}