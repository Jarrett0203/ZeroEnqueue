package com.example.zeroenqueue.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zeroenqueue.R
import com.example.zeroenqueue.adapters.FoodStallAdapter
import com.example.zeroenqueue.adapters.VendorFoodStallAdapter
import com.example.zeroenqueue.databinding.ActivityVendorFoodStallsBinding
import com.example.zeroenqueue.eventBus.FoodStallClick
import com.example.zeroenqueue.eventBus.VendorFoodStallClick
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_vendor_food_stalls.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class VendorFoodStallsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVendorFoodStallsBinding
    private lateinit var dialog: AlertDialog
    private lateinit var layoutAnimationController: LayoutAnimationController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVendorFoodStallsBinding.inflate(layoutInflater)
        val root: View = binding.root

        val vendorFoodStallsViewModel =
            ViewModelProvider(this)[VendorFoodStallsViewModel::class.java]

        val recyclerFoodStall = binding.recyclerFoodStalls
        dialog = SpotsDialog.Builder().setContext(this).setCancelable(false).build()
        dialog.show()
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_item_from_left)
        recyclerFoodStall.setHasFixedSize(true)
        recyclerFoodStall.layoutManager = LinearLayoutManager(this)
        vendorFoodStallsViewModel.foodStallList.observe(this) {
            dialog.dismiss()
            if (it == null || it.isEmpty()) {
                recyclerFoodStall.visibility = View.GONE
                noFoodStall!!.visibility = View.VISIBLE
                createNewStall!!.visibility = View.VISIBLE
                createNewStall.setOnClickListener {
                    val intent = Intent(this, CreateNewStallActivity::class.java)
                    startActivity(intent)
                }
            } else {
                recyclerFoodStall.visibility = View.VISIBLE
                noFoodStall!!.visibility = View.GONE
                createNewStall!!.visibility = View.GONE
                recyclerFoodStall.adapter = VendorFoodStallAdapter(this, it)
                recyclerFoodStall.layoutAnimation = layoutAnimationController
            }
        }
        setContentView(root)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onVendorFoodStallSelected(event: VendorFoodStallClick) {
        if (event.isSuccess) {
            val intent = Intent(this, MainVendorActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

}