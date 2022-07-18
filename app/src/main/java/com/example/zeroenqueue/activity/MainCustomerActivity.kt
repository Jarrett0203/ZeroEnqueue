package com.example.zeroenqueue.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.zeroenqueue.R
import com.example.zeroenqueue.classes.Food
import com.example.zeroenqueue.common.Common
import com.example.zeroenqueue.databinding.ActivityMainCustomerBinding
import com.example.zeroenqueue.db.CartDataSource
import com.example.zeroenqueue.db.CartDatabase
import com.example.zeroenqueue.db.LocalCartDataSource
import com.example.zeroenqueue.eventBus.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
import kotlinx.android.synthetic.main.app_bar_main_customer.*
import kotlinx.android.synthetic.main.nav_header_main_customer.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainCustomerActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainCustomerBinding
    private lateinit var cartDataSource: CartDataSource
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var navController: NavController
    private lateinit var dialog: AlertDialog

    private var menuItemClick = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(this).cartDAO())

        setSupportActionBar(binding.appBarMain.toolbar)

        drawerLayout = binding.drawerLayout
        navView = binding.navView

        val fab: FloatingActionButton = findViewById(R.id.fabCart)
        fab.setOnClickListener {
            navController.navigate(R.id.navigation_cart)
        }
        val headView = navView.getHeaderView(0)
        val profileImage: ImageView? = headView!!.profile_image
        navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_customer_home, R.id.navigation_categories, R.id.navigation_customer_order_summary,
                R.id.navigation_foodStall, R.id.navigation_profile, R.id.navigation_food_list,
                R.id.navigation_cart, R.id.navigation_customerDiscounts
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        if(Common.currentUser != null) {
            if (Common.currentUser!!.image != null)
                Glide.with(this).load(Common.currentUser!!.image).into(profileImage!!)
        }

        profileImage!!.setOnClickListener {
            val navHostFragment = supportFragmentManager.primaryNavigationFragment
            val currentFragment = navHostFragment!!.childFragmentManager.fragments[0]
            drawerLayout.closeDrawers()
            when (currentFragment.javaClass.name) {
                "com.example.zeroenqueue.uiCustomer.customerHome.CustomerHomeFragment"
                    -> navController.navigate(R.id.customer_home_to_profile)
                "com.example.zeroenqueue.uiCustomer.foodStall.FoodStallFragment"
                    -> navController.navigate(R.id.foodStall_to_profile)
                "com.example.zeroenqueue.uiCustomer.categories.CategoriesFragment"
                    -> navController.navigate(R.id.categories_to_profile)
                "com.example.zeroenqueue.uiCustomer.foodList.FoodListFragment"
                    -> navController.navigate(R.id.food_list_to_profile)
                "com.example.zeroenqueue.uiCustomer.orders.OrderSummaryFragment"
                    -> navController.navigate(R.id.customer_order_summary_to_profile)
                "com.example.zeroenqueue.uiCustomer.cart.CartFragment"
                    -> navController.navigate(R.id.cart_to_profile)
                "com.example.zeroenqueue.uiCustomer.discounts.CustomerDiscountsFragment"
                    -> navController.navigate(R.id.customer_discounts_to_profile)
            }
        }

        val txtUser = headView.findViewById<TextView>(R.id.txt_user)
        if(Common.currentUser != null)
            Common.setSpanString("Hey, ", Common.currentUser!!.name, txtUser)

        navView.setNavigationItemSelectedListener {
            drawerLayout.closeDrawers()
            when (it.itemId) {
                R.id.navigation_sign_out -> signout()
                R.id.navigation_customer_home ->
                    if (menuItemClick != it.itemId)
                        navController.navigate(R.id.navigation_customer_home)
                R.id.navigation_food_list ->
                    if (menuItemClick != it.itemId)
                        navController.navigate(R.id.navigation_food_list)
                R.id.navigation_categories ->
                    if (menuItemClick != it.itemId)
                        navController.navigate(R.id.navigation_categories)
                R.id.navigation_customer_order_summary ->
                    if (menuItemClick != it.itemId)
                        navController.navigate(R.id.navigation_customer_order_summary)
                R.id.navigation_foodStall ->
                    if (menuItemClick != it.itemId)
                        navController.navigate(R.id.navigation_foodStall)
                R.id.navigation_profile ->
                    if (menuItemClick != it.itemId)
                        navController.navigate(R.id.navigation_profile)
                R.id.navigation_cart ->
                    if (menuItemClick != it.itemId)
                        navController.navigate(R.id.navigation_cart)
                R.id.navigation_customerDiscounts ->
                    if (menuItemClick != it.itemId)
                        navController.navigate(R.id.navigation_customerDiscounts)
            }
            menuItemClick = it.itemId
            true
        }

        countCartItem()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
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

    private fun signout() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sign out")
            .setMessage("Are you sure you want to exit?")
            .setNegativeButton("CANCEL") { dialogInterface, _ -> dialogInterface.dismiss() }
            .setPositiveButton("OK") { _, _ ->
                Common.foodSelected = null
                Common.categorySelected = null
                Common.currentUser = null
                Common.foodStallSelected = null
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this@MainCustomerActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        val dialog = builder.create()
        dialog.show()
    }

    private fun countCartItem() {
        if(Common.currentUser != null)
            cartDataSource.countCartItems(Common.currentUser!!.uid!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Int> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onSuccess(t: Int) {
                        fabCart.count = t
                    }

                    override fun onError(e: Throwable) {
                        if (!e.message!!.contains("empty"))
                            Toast.makeText(
                                this@MainCustomerActivity,
                                "[COUNT CART]" + e.message,
                                Toast.LENGTH_SHORT
                            ).show()
                    }

                })

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
    fun onHideFABEvent(event: HideFABCart){
        if (event.isHidden){
            fabCart.hide()
        } else {
            fabCart.show()
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onRecommendedSelected(event: RecommendedClick) {
        dialog = SpotsDialog.Builder().setContext(this).setCancelable(false).build()
        dialog.show()
        FirebaseDatabase.getInstance().getReference(Common.FOODLIST_REF)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (itemSnapShot in snapshot.children) {
                            val food = itemSnapShot.getValue(Food::class.java)
                            if (event.recommended.food_id == food!!.id) {
                                dialog.dismiss()
                                Common.foodSelected = food
                                Common.foodSelected!!.key = itemSnapShot.key
                                findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.navigation_food_detail)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    dialog.dismiss()
                    Toast.makeText(this@MainCustomerActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onDiscountSelected(event: CustomerDiscountItemClick) {
        if (event.isSuccess) {
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.navigation_customer_discounts_detail)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onOrderSelected(event: OrderItemClick) {
        if (event.isSuccess) {
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.navigation_customer_order_detail)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onMenuItemBack(event: MenuItemBack) {
        menuItemClick = -1
        if (supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.popBackStack()
    }

}
