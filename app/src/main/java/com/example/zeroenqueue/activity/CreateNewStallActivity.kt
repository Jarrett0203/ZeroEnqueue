package com.example.zeroenqueue.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.zeroenqueue.R
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.ActivityCreateNewStallBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class CreateNewStallActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateNewStallBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateNewStallBinding.inflate(layoutInflater)
        val root : View = binding.root

        val foodStallRef = FirebaseDatabase.getInstance().getReference(Common.FOODSTALL_REF)
        val storageRef = FirebaseStorage.getInstance().reference

        val stallImage = binding.addStallImage
        val editStallName = binding.inputStallName
        val editStallPhone = binding.inputStallPhone
        val editStallAddress = binding.inputStallAddress
        val editStallDescription = binding.inputDescription
        val confirm = binding.BtnConfirm
        val progressBar = binding.createNewStallProgressBar

        stallImage.setOnClickListener{

        }




        setContentView(root)
    }
}