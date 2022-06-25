package com.example.zeroenqueue.uiCustomer.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.zeroenqueue.databinding.FragmentOrderStatusBinding

class OrderStatusFragment : Fragment() {

private var _binding: FragmentOrderStatusBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val orderStatusViewModel =
            ViewModelProvider(this).get(OrderStatusViewModel::class.java)

    _binding = FragmentOrderStatusBinding.inflate(inflater, container, false)
    val root: View = binding.root

    val textView: TextView = binding.textOrderStatus
    orderStatusViewModel.text.observe(viewLifecycleOwner) {
      textView.text = it
    }
    return root
  }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}