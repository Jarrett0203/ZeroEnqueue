package com.example.zeroenqueue.services

import android.content.Context
import android.widget.Toast
import com.example.zeroenqueue.common.Common
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*
import kotlin.random.Random.Default.nextInt

class MyFCMServices: FirebaseMessagingService() {
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Common.updateToken(this, p0)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        val dataRecv = p0.data
        if(dataRecv != null) {
            Common.showNotification(this, Random().nextInt(),
                dataRecv[Common.NOTI_TITLE],
                dataRecv[Common.NOTI_CONTENT],
                null)
        }
    }



}