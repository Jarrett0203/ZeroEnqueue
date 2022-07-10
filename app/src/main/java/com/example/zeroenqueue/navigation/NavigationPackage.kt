package com.example.zeroenqueue.navigation

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager


class NavigationPackage : ReactPackage {
    private val reactContext: ReactApplicationContext? = null
    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        return emptyList()
    }

    override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
        val modules: MutableList<NativeModule> = ArrayList()
        modules.add(NavigationModule(reactContext)) // Add the module that you would like to call to from RN
        return modules
    }
}