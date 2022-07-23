package com.example.zeroenqueue.common

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.zeroenqueue.R
import com.example.zeroenqueue.eventBus.LoadAllOrders
import com.example.zeroenqueue.eventBus.LoadOrderEvent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_order_filter.*
import org.greenrobot.eventbus.EventBus

class BottomSheetOrderFragment: BottomSheetDialogFragment() {
    override fun onCreate (savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val itemView = inflater.inflate(R.layout.fragment_order_filter, container, false)
        return itemView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        radioPlaced.id = 0
        radioPreparing.id = 1
        radioCollect.id = 2
        radioCancelled.id = 3

        for (i in 0 until radioOrderStatus.childCount) {
            val radio = radioOrderStatus.getChildAt(i) as RadioButton
            if (Common.orderStatusSelected == i) {
                radio.isChecked = true
                radio.isSelected = true
            }
            radio.setOnClickListener {
                if (!radio.isSelected) {
                    radio.isChecked = true
                    radio.isSelected = true
                } else {
                    radioOrderStatus.clearCheck()
                    radio.isSelected = false
                }
            }
        }

        btnFilter.setOnClickListener {
            when (radioOrderStatus.checkedRadioButtonId) {
                -1 -> {
                    Common.orderStatusSelected = -1
                    EventBus.getDefault().postSticky(LoadAllOrders())
                }
                0 -> {
                    Common.orderStatusSelected = 0
                    EventBus.getDefault().postSticky(LoadOrderEvent(0))
                }
                1 -> {
                    Common.orderStatusSelected = 1
                    EventBus.getDefault().postSticky(LoadOrderEvent(1))
                }
                2 -> {
                    Common.orderStatusSelected = 2
                    EventBus.getDefault().postSticky(LoadOrderEvent(2))
                }
                3 -> {
                    Common.orderStatusSelected = 3
                    EventBus.getDefault().postSticky(LoadOrderEvent(3))
                }
                else -> Toast.makeText(context, "Error filtering orders", Toast.LENGTH_SHORT).show()
            }
            dismiss()
        }
    }
    companion object {
        val instance:BottomSheetOrderFragment?=null
            get() = field?: BottomSheetOrderFragment()

    }
}