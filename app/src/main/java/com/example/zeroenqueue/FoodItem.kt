package com.example.zeroenqueue

data class FoodItem (
    val id : Int,
    val image : String,
    val price : Double,
    val rating : Int,
    val reviewCount : Int,
    val foodName: String,
    val foodStallName: String,
    val categories: String,
    val isNew: Boolean
    )