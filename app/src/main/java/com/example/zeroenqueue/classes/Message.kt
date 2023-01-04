package com.example.zeroenqueue.classes

data class Message (
    var id: String?,
    var senderId: String?,
    var receiverId: String?,
    var userImage: String?,
    var message: String?,
    var timestamp: Long?
) {
    constructor() : this("", "", "", "", "", -1)
}