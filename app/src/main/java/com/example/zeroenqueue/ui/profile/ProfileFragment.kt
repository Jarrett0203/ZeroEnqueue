package com.example.zeroenqueue.ui.profile

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.zeroenqueue.databinding.FragmentProfileBinding
import com.google.android.material.textfield.TextInputEditText
import dmax.dialog.SpotsDialog

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var dialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val ProfileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val headerName: TextView = binding.tvName
        val editName: TextInputEditText = binding.inputName
        val editUserType: TextInputEditText = binding.inputUserType
        val editEmail: TextInputEditText = binding.inputEmail
        val editPassword: TextInputEditText = binding.inputPassword
        dialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()
        dialog.show()
        ProfileViewModel.profile.observe(viewLifecycleOwner) {
            dialog.dismiss()
            headerName.text = it.name
            editName.setText(it.name)
            editUserType.setText(it.userType)
            editEmail.setText(it.email)
            editPassword.setText(it.password)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}