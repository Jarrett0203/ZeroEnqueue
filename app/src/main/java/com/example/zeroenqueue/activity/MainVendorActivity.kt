package com.example.zeroenqueue.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.zeroenqueue.R
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.ActivityMainVendorBinding
import com.example.zeroenqueue.eventBus.FoodItemClick
import com.example.zeroenqueue.eventBus.VendorFoodItemClick
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.app_bar_main_vendor.*
import kotlinx.android.synthetic.main.nav_header_main_customer.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainVendorActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainVendorBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainVendorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMainVendor.toolbar)

        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val headView = navView.getHeaderView(0)
        val profileImage: ImageView? = headView!!.profile_image
        val navController = findNavController(R.id.nav_host_fragment_content_main_vendor)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_vendor_home, R.id.navigation_orders, R.id.navigation_stall_menu
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        profileImage!!.setOnClickListener {
            val navHostFragment = supportFragmentManager.primaryNavigationFragment
            val currentFragment = navHostFragment!!.childFragmentManager.fragments[0]
            drawerLayout.closeDrawers()
            if (currentFragment.javaClass.name == "com.example.zeroenqueue.uiVendor.vendorHome.VendorHomeFragment")
                navController.navigate(R.id.vendor_home_to_profile)
            if (currentFragment.javaClass.name == "com.example.zeroenqueue.uiVendor.menu.MenuFragment")
                navController.navigate(R.id.stall_menu_to_profile)
            if (currentFragment.javaClass.name == "com.example.zeroenqueue.uiVendor.orders.OrdersFragment")
                navController.navigate(R.id.orders_to_profile)
        }

        val txtUser = headView.findViewById<TextView>(R.id.txt_user)
        Common.setSpanString("Hey, ", Common.currentUser!!.name, txtUser)

        navView.setNavigationItemSelectedListener { item ->
            drawerLayout.closeDrawers()
            when (item.itemId) {
                R.id.navigation_vendor_home -> navController.navigate(R.id.navigation_vendor_home)
                R.id.navigation_orders -> navController.navigate(R.id.navigation_orders)
                R.id.navigation_stall_menu -> navController.navigate(R.id.navigation_stall_menu)
                R.id.navigation_profile -> navController.navigate(R.id.navigation_profile)
                R.id.navigation_sign_out -> signout()
                R.id.navigation_switch_stall -> switchStall()
            }
            true
        }
    }

    private fun switchStall() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Switch Stall")
            .setMessage("Are you sure you want to switch stalls?")
            .setNegativeButton("CANCEL") { dialogInterface, _ -> dialogInterface.dismiss() }
            .setPositiveButton("OK") { _, _ ->
                Common.foodStallSelected = null
                val intent = Intent(this@MainVendorActivity, StallsOverviewActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        val dialog = builder.create()
        dialog.show()
    }

    private fun signout() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sign out")
            .setMessage("Are you sure you want to exit?")
            .setNegativeButton("CANCEL") { dialogInterface, _ -> dialogInterface.dismiss() }
            .setPositiveButton("OK") { _, _ ->
                Common.currentUser = null
                Common.foodStallSelected = null
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this@MainVendorActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        val dialog = builder.create()
        dialog.show()
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onVendorFoodSelected(event: VendorFoodItemClick) {
        if (event.isSuccess) {
            findNavController(R.id.nav_host_fragment_content_main_vendor).navigate(R.id.navigation_vendorFoodDetail)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main_vendor)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }
}