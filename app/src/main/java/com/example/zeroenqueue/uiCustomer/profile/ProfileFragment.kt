package com.example.zeroenqueue.uiCustomer.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.zeroenqueue.R
import com.example.zeroenqueue.activity.MainCustomerActivity
import com.example.zeroenqueue.activity.StallsOverviewActivity
import com.example.zeroenqueue.classes.User
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.FragmentProfileBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_register_user.*
import java.util.*


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var dialog: AlertDialog
    private lateinit var editName: TextInputEditText
    private lateinit var editPhone: TextInputEditText
    private lateinit var editEmail: TextInputEditText
    private lateinit var editPassword: TextInputEditText
    private lateinit var profileViewModel: ProfileViewModel
    private var switchUserDialog: AlertDialog? = null
    private val currentFirebaseUser = FirebaseAuth.getInstance().currentUser
    private val storageRef = FirebaseStorage.getInstance().reference
    private val userRef = FirebaseDatabase.getInstance().getReference(Common.USER_REF)

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        profileViewModel =
            ViewModelProvider(this)[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val headerName = binding.tvName
        editName = binding.inputName
        editPhone = binding.inputPhone
        editEmail = binding.inputEmail
        editPassword = binding.inputPassword
        val profileImage = binding.profileImage
        val customerChip = binding.chipCustomer
        val vendorChip = binding.chipVendor
        val updateBtn = binding.btnUpdate
        val notNUSLayout = binding.notNUSLayout
        val btnUploadNUSCard = binding.btnUploadNUSCard
        val verifiedUserText = binding.verifiedUserText

        if (Common.currentUser!!.userType!! == "Customer") {
            customerChip.isChecked = true
        } else {
            vendorChip.isChecked = true
            val balance = binding.balance
            balance.visibility = View.INVISIBLE
        }

        updateBtn.setOnClickListener {
            updateDetails()
        }

        dialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()
        dialog.show()
        profileViewModel.profile.observe(viewLifecycleOwner) {
            dialog.dismiss()
            if (it.image != null)
                Glide.with(requireContext()).load(it.image).into(profileImage)
            headerName.text = it.name
            editName.setText(it.name)
            editPhone.setText(it.phone)
            editEmail.setText(it.email)
            editPassword.setText(it.password)
            if (it.nus) {
                notNUSLayout.visibility = View.GONE
                verifiedUserText.visibility = View.VISIBLE
            }
        }

        val itemView = layoutInflater.inflate(R.layout.layout_upload_nus_card, null)
        val cardImage = itemView.findViewById<ImageView>(R.id.addNUSCardImage)
        val cardImagePrompt = itemView.findViewById<TextView>(R.id.add_card_image_text)
        var cardImageUri : Uri? = null

        val cardImageResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    cardImageUri = data!!.data
                    cardImage.setImageURI(cardImageUri)
                    cardImagePrompt.text = "Change image..."
                }
            }

        profileImage.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Upload profile image")
            builder.setMessage("Please upload your profile image here")

            cardImage.setOnClickListener {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                cardImageResultLauncher.launch(intent)
            }

            builder.setNegativeButton("CANCEL") {dialogInterface, _ -> dialogInterface.dismiss()}
            builder.setPositiveButton("OK") { dialogInterface, _ ->
                if (cardImageUri != null) {
                    dialog.setMessage("Uploading")
                    dialog.show()

                    val imageName = Common.currentUser!!.uid
                    val imageFolder = storageRef.child("profileImages/$imageName")
                    imageFolder.putFile(cardImageUri!!).addOnFailureListener{
                        dialog.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }.addOnProgressListener {
                        val progress = 100.0 * it.bytesTransferred/ it.totalByteCount
                        dialog.setMessage("Uploaded $progress%")
                    }.addOnSuccessListener {
                        dialogInterface.dismiss()
                        imageFolder.downloadUrl.addOnSuccessListener {
                            val updateData = HashMap<String, Any>()
                            updateData["image"] = it.toString()
                            userRef.child(Common.currentUser!!.uid!!).updateChildren(updateData)
                                .addOnFailureListener { e ->
                                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                                }
                                .addOnCompleteListener { _ ->
                                    Common.currentUser!!.image = it.toString()
                                    profileViewModel.loadProfile()
                                    Toast.makeText(requireContext(), "Upload Success", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
            }
            builder.setView(itemView)
            val uploadDialog = builder.create()
            uploadDialog.show()
        }

        btnUploadNUSCard.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Upload NUS card image")
            builder.setMessage("Please upload your NUS card here")

            cardImage.setOnClickListener {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                cardImageResultLauncher.launch(intent)
            }

            builder.setNegativeButton("CANCEL") {dialogInterface, _ -> dialogInterface.dismiss()}
            builder.setPositiveButton("OK") {dialogInterface, _ ->
                if (cardImageUri != null) {
                    dialog.setMessage("Uploading")
                    dialog.show()

                    val imageName = Common.currentUser!!.uid
                    val imageFolder = storageRef.child("NUSValidation/$imageName")
                    imageFolder.putFile(cardImageUri!!).addOnFailureListener{
                        dialog.dismiss()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }.addOnProgressListener {
                        val progress = 100.0 * it.bytesTransferred/ it.totalByteCount
                        dialog.setMessage("Uploaded $progress%")
                    }.addOnSuccessListener {
                        dialogInterface.dismiss()
                        imageFolder.downloadUrl.addOnSuccessListener {
                            val updateData = HashMap<String, Any>()
                            updateData["cardImage"] = it.toString()
                            updateData["nus"] = true
                            userRef.child(Common.currentUser!!.uid!!).updateChildren(updateData)
                                .addOnFailureListener { e ->
                                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                                }
                                .addOnCompleteListener { _ ->
                                    Common.currentUser!!.cardImage = it.toString()
                                    Common.currentUser!!.nus = true
                                    notNUSLayout.visibility = View.GONE
                                    profileViewModel.loadProfile()
                                    Toast.makeText(requireContext(), "Upload Success", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
            }

            builder.setView(itemView)
            val uploadDialog = builder.create()
            uploadDialog.show()
        }

        return root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateDetails() {
        val newName = editName.text.toString().trim()
        val newPhone = editPhone.text.toString().trim()
        val newEmail = editEmail.text.toString().trim()
        val newPassword = editPassword.text.toString().trim()
        val firstNums = arrayOf('6', '8', '9')

        if (newName.isEmpty() || newPhone.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty())
            Toast.makeText(requireContext(), "Empty fields are not allowed!!", Toast.LENGTH_SHORT).show()
        else if (!Arrays.stream(firstNums).anyMatch { t -> t == newPhone[0] } || newPhone.length != 8)
            Toast.makeText(requireContext(), "Invalid phone number", Toast.LENGTH_SHORT).show()
        else if (newPassword.length < 6)
            Toast.makeText(requireContext(), "Password requires at least 6 characters", Toast.LENGTH_SHORT).show()
        else {
            dialog.show()
            FirebaseDatabase.getInstance()
                .getReference(Common.USER_REF)
                .child(Common.currentUser!!.uid!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val user = snapshot.getValue(User::class.java)
                            val updateData = HashMap<String, Any>()

                            updateData["name"] = newName
                            updateData["phone"] = newPhone
                            updateData["email"] = newEmail
                            updateData["password"] = newPassword
                            user!!.name = newName
                            user.phone = newPhone
                            user.email = newEmail
                            user.password = newPassword
                            if (chipCustomer.isChecked) {
                                updateData["userType"] = "Customer"
                                user.userType = "Customer"
                            }
                            else {
                                updateData["userType"] = "Vendor"
                                user.userType = "Vendor"
                            }

                            if (Common.currentUser!!.userType!! != user.userType) {
                                val builder = AlertDialog.Builder(requireContext())
                                if (chipCustomer.isChecked) {
                                    builder.setTitle("Switch Account")
                                        .setMessage("Are you sure you want to switch to customer account?")
                                        .setNegativeButton("CANCEL") { dialogInterface, _ ->
                                            chipCustomer.isChecked = false
                                            chipVendor.isChecked = true
                                            dialogInterface.dismiss()
                                        }
                                        .setPositiveButton("OK") { _, _ ->
                                            currentFirebaseUser?.let { firebaseUser ->
                                                val credential = EmailAuthProvider.getCredential(firebaseUser.email!!, Common.currentUser!!.password!!)
                                                firebaseUser.reauthenticate(credential).addOnCompleteListener { reAuthTask ->
                                                    if (reAuthTask.isSuccessful) {
                                                        firebaseUser.updateEmail(newEmail).addOnCompleteListener { emailTask ->
                                                            if (emailTask.isSuccessful) {
                                                                firebaseUser.updatePassword(newPassword).addOnCompleteListener { pwTask ->
                                                                    if (pwTask.isSuccessful) {
                                                                        snapshot.ref
                                                                            .updateChildren(updateData)
                                                                            .addOnCompleteListener { task ->
                                                                                if (task.isSuccessful) {
                                                                                    profileViewModel.setProfile(user)
                                                                                    Common.currentUser = user
                                                                                }
                                                                            }
                                                                    } else{
                                                                        Toast.makeText(context, "Password failed to update", Toast.LENGTH_SHORT).show()
                                                                        dialog.dismiss()
                                                                    }
                                                                }
                                                            }
                                                            else {
                                                                Toast.makeText(context, "Email failed to update", Toast.LENGTH_SHORT).show()
                                                                dialog.dismiss()
                                                            }
                                                        }
                                                    }
                                                    else {
                                                        Toast.makeText(requireContext(), "Re-authentication failed", Toast.LENGTH_SHORT).show()
                                                        dialog.dismiss()
                                                    }
                                                }
                                            }
                                            val intent = Intent(
                                                requireContext(),
                                                MainCustomerActivity::class.java
                                            )
                                            Common.foodStallSelected = null
                                            intent.flags =
                                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                        }
                                }

                                else {
                                    builder.setTitle("Switch Account")
                                        .setMessage("Are you sure you want to switch to vendor account?")
                                        .setNegativeButton("CANCEL") { dialogInterface, _ ->
                                            chipCustomer.isChecked = true
                                            chipVendor.isChecked = false
                                            dialogInterface.dismiss()
                                        }
                                        .setPositiveButton("OK") { _, _ ->
                                            currentFirebaseUser?.let { firebaseUser ->
                                                val credential = EmailAuthProvider.getCredential(firebaseUser.email!!, Common.currentUser!!.password!!)
                                                firebaseUser.reauthenticate(credential).addOnCompleteListener { reAuthTask ->
                                                    if (reAuthTask.isSuccessful) {
                                                        firebaseUser.updateEmail(newEmail).addOnCompleteListener { emailTask ->
                                                            if (emailTask.isSuccessful) {
                                                                firebaseUser.updatePassword(newPassword).addOnCompleteListener { pwTask ->
                                                                    if (pwTask.isSuccessful) {
                                                                        snapshot.ref
                                                                            .updateChildren(updateData)
                                                                            .addOnCompleteListener { task ->
                                                                                if (task.isSuccessful) {
                                                                                    profileViewModel.setProfile(user)
                                                                                    Common.currentUser = user
                                                                                }
                                                                            }
                                                                    } else{
                                                                        Toast.makeText(context, "Password failed to update", Toast.LENGTH_SHORT).show()
                                                                        dialog.dismiss()
                                                                    }
                                                                }
                                                            }
                                                            else {
                                                                Toast.makeText(context, "Email failed to update", Toast.LENGTH_SHORT).show()
                                                                dialog.dismiss()
                                                            }
                                                        }
                                                    }
                                                    else {
                                                        Toast.makeText(requireContext(), "Re-authentication failed", Toast.LENGTH_SHORT).show()
                                                        dialog.dismiss()
                                                    }
                                                }
                                            }
                                            Common.foodStallSelected = null
                                            val intent = Intent(
                                                requireContext(),
                                                StallsOverviewActivity::class.java
                                            )
                                            intent.flags =
                                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                        }
                                }
                                switchUserDialog = builder.create()
                                switchUserDialog!!.show()
                            }
                            else {
                                currentFirebaseUser?.let { firebaseUser ->
                                val credential = EmailAuthProvider.getCredential(firebaseUser.email!!, Common.currentUser!!.password!!)
                                firebaseUser.reauthenticate(credential).addOnCompleteListener { reAuthTask ->
                                    if (reAuthTask.isSuccessful) {
                                        firebaseUser.updateEmail(newEmail).addOnCompleteListener { emailTask ->
                                            if (emailTask.isSuccessful) {
                                                firebaseUser.updatePassword(newPassword).addOnCompleteListener { pwTask ->
                                                    if (pwTask.isSuccessful) {
                                                        snapshot.ref
                                                            .updateChildren(updateData)
                                                            .addOnCompleteListener { task ->
                                                                if (task.isSuccessful) {
                                                                    profileViewModel.setProfile(user)
                                                                    Common.currentUser = user

                                                                    dialog.dismiss()
                                                                }
                                                                dialog.dismiss()
                                                            }
                                                    } else{
                                                        Toast.makeText(context, "Password failed to update", Toast.LENGTH_SHORT).show()
                                                        dialog.dismiss()
                                                    }
                                                }
                                            }
                                            else {
                                                Toast.makeText(context, "Email failed to update", Toast.LENGTH_SHORT).show()
                                                dialog.dismiss()
                                            }
                                        }
                                    }
                                    else {
                                        Toast.makeText(requireContext(), "Re-authentication failed", Toast.LENGTH_SHORT).show()
                                        dialog.dismiss()
                                    }
                                }
                            }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        dialog.dismiss()
                        Toast.makeText(context!!, error.message, Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        val navigationView = requireActivity().findViewById<NavigationView>(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)
        val txtUser = headerView.findViewById<TextView>(R.id.txt_user)
        Common.setSpanString("Hey, ", Common.currentUser!!.name, txtUser)
    }

    override fun onStop() {
        super.onStop()
        if (switchUserDialog != null) {
            switchUserDialog!!.dismiss()
            switchUserDialog = null
        }
    }

    companion object {
        private var instance: ProfileFragment? = null
        fun getInstance(): ProfileFragment {
            if (instance == null) {
                instance = ProfileFragment()
            }
            return instance!!
        }
    }
}