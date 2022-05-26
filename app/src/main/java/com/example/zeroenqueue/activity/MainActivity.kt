package com.example.zeroenqueue.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.zeroenqueue.R
import com.example.zeroenqueue.classes.Food
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.ActivityMainBinding
import com.example.zeroenqueue.db.CartDataSource
import com.example.zeroenqueue.db.CartDatabase
import com.example.zeroenqueue.db.LocalCartDataSource
import com.example.zeroenqueue.eventBus.*
import com.example.zeroenqueue.ui.home.HomeFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dmax.dialog.SpotsDialog
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var cartDataSource: CartDataSource
    private var drawerLayout: DrawerLayout? = null
    private lateinit var navView: NavigationView
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(this).cartDAO())

        setSupportActionBar(binding.appBarMain.toolbar)

        drawerLayout = binding.drawerLayout
        navView = binding.navView
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
            val navHostFragment = supportFragmentManager.primaryNavigationFragment
            val currentFragment = navHostFragment!!.childFragmentManager.fragments[0]
            drawerLayout!!.closeDrawers()
            if (currentFragment.javaClass.name == "com.example.zeroenqueue.ui.home.HomeFragment")
                navController.navigate(R.id.home_to_profile)
            if (currentFragment.javaClass.name == "com.example.zeroenqueue.ui.foodStall.FoodStallFragment")
                navController.navigate(R.id.foodStall_to_profile)
            if (currentFragment.javaClass.name == "com.example.zeroenqueue.ui.categories.CategoriesFragment")
                navController.navigate(R.id.categories_to_profile)
            if (currentFragment.javaClass.name == "com.example.zeroenqueue.ui.foodList.FoodListFragment")
                navController.navigate(R.id.food_list_to_profile)
            if (currentFragment.javaClass.name == "com.example.zeroenqueue.ui.orderStatus.OrderStatusFragment")
                navController.navigate(R.id.order_status_to_profile)
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val txt_user = headView.findViewById<TextView>(R.id.txt_user)
        Common.setSpanString("Hey, ", Common.currentUser!!.name, txt_user)

        navView.setNavigationItemSelectedListener(object :
            NavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                item.isChecked = true
                drawerLayout!!.closeDrawers()
                if (item.itemId == R.id.navigation_sign_out) {
                    signout()
                } else if (item.itemId == R.id.navigation_home) {
                    navController.navigate(R.id.navigation_home)
                } else if (item.itemId == R.id.navigation_food_list) {
                    navController.navigate(R.id.navigation_food_list)
                } else if (item.itemId == R.id.navigation_categories) {
                    navController.navigate(R.id.navigation_categories)
                } else if (item.itemId == R.id.navigation_order_status) {
                    navController.navigate(R.id.navigation_order_status)
                } else if (item.itemId == R.id.navigation_foodStall) {
                    navController.navigate(R.id.navigation_foodStall)
                } else if (item.itemId == R.id.navigation_profile) {
                    navController.navigate(R.id.navigation_profile)
                } else if (item.itemId == R.id.navigation_order_status) {
                    navController.navigate(R.id.navigation_order_status)
                }
                return false
            }
        })
        countCartItem()
    }

    private fun signout() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sign out")
            .setMessage("Are you sure you want to exit?")
            .setNegativeButton("CANCEL", { dialogInterface, _ -> dialogInterface.dismiss() })
            .setPositiveButton("OK") { dialogInterface, _ ->
                Common.foodSelected = null
                Common.categorySelected = null
                Common.currentUser = null
                Common.foodStallSelected = null
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
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

    override fun onResume() {
        super.onResume()
        countCartItem()
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onCategorySelected(event: CategoryClick) {
        if (event.isSuccess) {
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.navigation_food_list)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onFoodSelected(event: FoodItemClick) {
        if (event.isSuccess) {
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.navigation_food_detail)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onFoodStallSelected(event: FoodStallClick) {
        if (event.isSuccess) {
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.navigation_food_list)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onCountCartEvent(event: CountCartEvent) {
        if (event.isSuccess) {
            countCartItem()
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onRecommendedSelected(event: RecommendedClick) {
        dialog = SpotsDialog.Builder().setContext(this).setCancelable(false).build()
        dialog.show()
        FirebaseDatabase.getInstance().getReference("FoodList")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (itemSnapShot in snapshot.children) {
                            val food = itemSnapShot.getValue(Food::class.java)
                            if (event.recommended.food_id == food!!.id) {
                                dialog.dismiss()
                                Common.foodSelected = food
                                findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.navigation_food_detail)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    dialog.dismiss()
                    Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun countCartItem() {
        cartDataSource.countCartItems(Common.currentUser!!.uid!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Int> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(t: Int) {
                    fab.count = t
                }

                override fun onError(e: Throwable) {
                    if (!e.message!!.contains("empty"))
                        Toast.makeText(
                            this@MainActivity,
                            "[COUNT CART]" + e.message,
                            Toast.LENGTH_SHORT
                        ).show()
                }

            })

    }
}
