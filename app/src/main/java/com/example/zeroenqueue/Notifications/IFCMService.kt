package com.example.zeroenqueue.Notifications

import com.example.zeroenqueue.classes.FCMResponse
import com.example.zeroenqueue.classes.FCMSendData
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface IFCMService {
    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAArvvKseQ:APA91bECedFjf38lTxzIDdidK9uW-KCQEcrL_6q4XcYbEE4FHD8heeVvHNcFrjOQJhqFSNh8fPHa7lDafz6gcGLMR1S6_UZjO7XlA6E2fFMzjxUVT0arEybsJAjQ38jT664nrUvY9pVH"
    )
    @POST("fcm/send")
    fun sendNotification(@Body body: FCMSendData) : Observable<FCMResponse>
}