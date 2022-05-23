package com.example.zeroenqueue.activity

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.zeroenqueue.R
import com.example.zeroenqueue.databinding.ActivityMainBinding
import com.example.zeroenqueue.eventBus.CategoryClick
import com.example.zeroenqueue.eventBus.FoodItemClick
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.nav_header_main.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val headView: View? = navView.getHeaderView(0)
        val profileImage: ImageView? = headView!!.profile_image
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_categories, R.id.navigation_order_status,
                R.id.navigation_foodStall, R.id.navigation_profile, R.id.navigation_food_list
            ), drawerLayout
        )

        profileImage!!.setOnClickListener {
            /*val profileFragment = ProfileFragment()
            val fragment : Fragment? =

            supportFragmentManager.findFragmentByTag(ProfileFragment::class.java.simpleName)

            if (fragment !is ProfileFragment){
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_layout, profileFragment, ProfileFragment::class.java.simpleName)
                    .commit()
            }*/
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
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

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onCategorySelected(event:CategoryClick){
        if (event.isSuccess){
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.navigation_categoryFoodList)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onFoodSelected(event: FoodItemClick){
        if (event.isSuccess){
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.navigation_food_detail)
        }
    }
}
