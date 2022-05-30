package com.example.zeroenqueue.uiCustomer.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zeroenqueue.classes.Comment

class CommentViewModel : ViewModel() {
    private var commentListMutableLiveData: MutableLiveData<List<Comment>>? = null

    init {
        commentListMutableLiveData = MutableLiveData()
    }

    val commentList: LiveData<List<Comment>>
        get() {
            return commentListMutableLiveData!!
        }

    fun setCommentList(commentList: List<Comment>){
        commentListMutableLiveData!!.value = commentList
    }
}