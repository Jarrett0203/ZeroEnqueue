package com.example.zeroenqueue.interfaces

import com.example.zeroenqueue.classes.Comment

interface ICommentCallback {
    fun onCommentLoadSuccess(commentList:List<Comment>)
    fun onCommentLoadFailed(message:String)
}
