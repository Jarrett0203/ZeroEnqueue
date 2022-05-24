package com.example.zeroenqueue.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, entities = [CartItem::class], exportSchema = false)
abstract class CartDatabase: RoomDatabase() {
    abstract fun cartDAO(): CartDAO

    companion object{
        private var instance:CartDatabase? = null

        fun getInstance(context:Context):CartDatabase{
            if(instance == null)
                instance = Room.databaseBuilder(context, CartDatabase::class.java, "ZeroEnqueueDB1").build()
            return instance!!
        }
    }
}