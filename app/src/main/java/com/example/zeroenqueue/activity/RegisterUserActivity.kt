package com.example.zeroenqueue.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.zeroenqueue.classes.User
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.ActivityRegisterUserBinding
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dmax.dialog.SpotsDialog
import java.util.*


class RegisterUserActivity : AppCompatActivity() {

    private val storageRef = FirebaseStorage.getInstance().reference
    private lateinit var binding: ActivityRegisterUserBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userRef: DatabaseReference
    private lateinit var dialog: AlertDialog
    private lateinit var chipCustomer: Chip
    private lateinit var chipVendor: Chip

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar = binding.toolbar

        userRef = FirebaseDatabase.getInstance().getReference(Common.USER_REF)
        firebaseAuth = Firebase.auth
        dialog = SpotsDialog.Builder().setContext(this).setCancelable(false).build()

        val cardView = binding.cardViewCardImage
        val cardImage = binding.addCardImage
        val cardImagePrompt = binding.addCardImageText
        var cardImageUri: Uri? = null

        val cardImageResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    cardImageUri = data!!.data
                    cardImage.setImageURI(cardImageUri)
                    cardImagePrompt.text = "Change image..."
                }
            }

        cardView.setOnClickListener{
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "image/*"
            cardImageResultLauncher.launch(galleryIntent)
        }

        binding.registerUser.setOnClickListener {
            chipCustomer = binding.chipCustomer
            chipVendor = binding.chipVendor
            val newEmail = binding.email.text.toString().trim()
            val newPassword = binding.password.text.toString().trim()
            val newName = binding.fullName.text.toString().trim()
            val newPhone = binding.phone.text.toString().trim()
            val firstNums = arrayOf('6', '8', '9')

            if(RegisterUser.validateRegistrationInput(newEmail, newPassword, newName, newPhone, firstNums)) {
                if (!chipCustomer.isChecked && !chipVendor.isChecked)
                    Toast.makeText(this, "Please select a user type.", Toast.LENGTH_SHORT).show()
                else {
                    firebaseAuth.createUserWithEmailAndPassword(newEmail, newPassword)
                        .addOnCompleteListener(this) {
                            if (it.isSuccessful) {
                                val newUser = firebaseAuth.currentUser
                                dialog.show()
                                userRef.child(newUser!!.uid)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.exists()) {
                                                val currentUser = snapshot.getValue(User::class.java)
                                                goToMainActivity(currentUser)
                                            } else {

                                                val currentUser = User()
                                                currentUser.uid = newUser.uid
                                                currentUser.name = newName
                                                currentUser.phone = newPhone
                                                currentUser.email = newEmail
                                                currentUser.password = newPassword
                                                if (chipCustomer.isChecked)
                                                    currentUser.userType = "Customer"
                                                else
                                                    currentUser.userType = "Vendor"

                                                if (cardImageUri != null) {
                                                    val fileRef: StorageReference = storageRef.child(
                                                        System.currentTimeMillis().toString() + "." + getFileExtension(cardImageUri!!)
                                                    )
                                                    fileRef.putFile(cardImageUri!!).addOnSuccessListener {
                                                        fileRef.downloadUrl.addOnSuccessListener {
                                                            currentUser.cardImage = it.toString()
                                                            currentUser.nus = true
                                                            userRef.child(newUser.uid)
                                                                .setValue(currentUser)
                                                                .addOnCompleteListener { task ->
                                                                    if (task.isSuccessful) {
                                                                        Toast.makeText(
                                                                            this@RegisterUserActivity,
                                                                            "Registration success",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                    }
                                                                    goToMainActivity(currentUser)
                                                                }
                                                        }
                                                    }
                                                }

                                                else {
                                                    currentUser.nus = false
                                                    userRef.child(newUser.uid)
                                                        .setValue(currentUser)
                                                        .addOnCompleteListener { task ->
                                                            if (task.isSuccessful) {
                                                                Toast.makeText(
                                                                    this@RegisterUserActivity,
                                                                    "Registration success",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                            goToMainActivity(currentUser)
                                                        }
                                                }
                                            }
                                            dialog.dismiss()
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Toast.makeText(
                                                this@RegisterUserActivity,
                                                error.message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    })
                            } else {
                                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }

        }

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.title = "Register User"

        actionBar.setDisplayHomeAsUpEnabled(true)
    }


    private fun getFileExtension(imageUri: Uri): Any? {
        val cr: ContentResolver = contentResolver
        val mime: MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(imageUri))
    }

    private fun goToMainActivity(user: User?) {
        Common.currentUser = user!!
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}

