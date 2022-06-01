package com.example.zeroenqueue.uiCustomer.profile

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.zeroenqueue.R
import com.example.zeroenqueue.classes.User
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.FragmentProfileBinding
import com.google.android.material.chip.Chip
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
    val currentFirebaseUser = FirebaseAuth.getInstance().currentUser

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

        val headerName: TextView = binding.tvName
        editName = binding.inputName
        editPhone = binding.inputPhone
        editEmail = binding.inputEmail
        editPassword = binding.inputPassword
        val customerChip: Chip = binding.chipCustomer
        val vendorChip: Chip = binding.chipVendor
        val updateBtn: Button = binding.btnUpdate

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
            headerName.text = it.name
            editName.setText(it.name)
            editPhone.setText(it.phone)
            editEmail.setText(it.email)
            editPassword.setText(it.password)
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
                            if (chipCustomer.isChecked)
                                updateData["userType"] = "Customer"
                            else
                                updateData["userType"] = "Vendor"

                            user!!.name = newName
                            user.phone = newPhone
                            user.email = newEmail
                            user.password = newPassword
                            if (chipCustomer.isChecked)
                                user.userType = "Customer"
                            else
                                user.userType = "Vendor"

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
                                                                dialog.dismiss()
                                                                if (task.isSuccessful) {
                                                                    profileViewModel.setProfile(user)
                                                                    Common.currentUser = user
                                                                    Toast.makeText(
                                                                        context,
                                                                        "Update successful",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                    dialog.dismiss()
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