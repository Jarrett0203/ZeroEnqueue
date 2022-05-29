package com.example.zeroenqueue.ui.profile

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.zeroenqueue.R
import com.example.zeroenqueue.classes.User
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.FragmentProfileBinding
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_register_user.*


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
        val editStore: MaterialCardView = binding.editStore
        val addNewFood: MaterialCardView = binding.addNewFood
        val updateBtn: Button = binding.btnUpdate

        if (Common.currentUser!!.userType!! == "Customer") {
            customerChip.isChecked = true
            editStore.visibility = View.GONE
            addNewFood.visibility = View.GONE
        }
        else {
            vendorChip.isChecked = true
        }

        updateBtn.setOnClickListener {
            dialog.show()
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

    private fun updateDetails() {
        FirebaseDatabase.getInstance()
            .getReference(Common.USER_REF)
            .child(Common.currentUser!!.uid!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(User::class.java)
                        val updateData = HashMap<String, Any>()

                        updateData["name"] = editName.text.toString()
                        updateData["phone"] = editPhone.text.toString()
                        updateData["email"] = editEmail.text.toString()
                        updateData["password"] = editPassword.text.toString()
                        if (chipCustomer.isChecked)
                            updateData["userType"] = "Customer"
                        else
                            updateData["userType"] = "Vendor"

                        user!!.name = editName.text.toString()
                        user.phone = editPhone.text.toString()
                        user.email = editEmail.text.toString()
                        user.password = editPassword.text.toString()
                        if (chipCustomer.isChecked)
                            user.userType = "Customer"
                        else
                            user.userType = "Vendor"

                        snapshot.ref
                            .updateChildren(updateData)
                            .addOnCompleteListener { task ->
                                dialog.dismiss()
                                if (task.isSuccessful) {
                                    profileViewModel.setProfile(user)
                                    Common.currentUser = user
                                    Toast.makeText(
                                        context!!,
                                        "Update successful",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                    else
                        dialog.dismiss()
                }

                override fun onCancelled(error: DatabaseError) {
                    dialog.dismiss()
                    Toast.makeText(context!!, error.message, Toast.LENGTH_SHORT).show()
                }
            })
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

    companion object{
        private var instance: ProfileFragment? = null
        fun getInstance(): ProfileFragment {
            if (instance == null){
                instance = ProfileFragment()
            }
            return instance!!
        }
    }
}