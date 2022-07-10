package com.example.zeroenqueue.navigation

import android.content.Intent
import com.example.zeroenqueue.activity.LoginActivity
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class NavigationModule internal constructor(private var reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {
    private var intent: Intent? = null
    override fun getName(): String {
        return "NavigationModule"
    } //The name of the component when it is called in the RN code

    @ReactMethod
    fun navigateToNative() {
        val context = reactApplicationContext
        intent = Intent(context, LoginActivity::class.java)
        if (intent!!.resolveActivity(context.packageManager) != null) {
            intent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}