package com.example.zeroenqueue.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.zeroenqueue.launchFragmentInHiltContainer
import com.example.zeroenqueue.uiCustomer.cart.CartFragment
import com.example.zeroenqueue.uiCustomer.cart.CartViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@HiltAndroidTest
@SmallTest
class CartDAOTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test-db")
    lateinit var database: CartDatabase
    private lateinit var dao: CartDAO

    @Before
    fun setup() {
        hiltRule.inject()
        dao = database.cartDAO()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertCartItem() {
        val cartItem = CartItem()
        cartItem.uid = "1"

        dao.insertOrReplaceAll(cartItem).blockingAwait()
        val cartItemList = ArrayList<CartItem>()
        cartItemList.add(cartItem)

        dao.getAllCart(cartItem.uid).take(2).test()
            .assertNoErrors()
            .assertValues(cartItemList)
    }

    @Test
    fun getSpecificCartItem() {
        val cartItem1 = CartItem()
        cartItem1.uid = "1"
        cartItem1.foodId = "foodId1"

        val cartItem2 = CartItem()
        cartItem2.uid = "1"
        cartItem2.foodId = "foodId2"

        dao.insertOrReplaceAll(cartItem1, cartItem2).blockingAwait()

        dao.getItemInCart(cartItem2.foodId, cartItem2.uid).test()
            .assertNoErrors()
            .assertValue {
                it == cartItem2
            }
    }

    @Test
    fun getSpecificItemWithAllOptionsCartItem() {
        val cartItem1 = CartItem()
        cartItem1.uid = "1"
        cartItem1.foodId = "foodId1"
        cartItem1.foodSize = "Large"
        cartItem1.foodAddon = "Rice"

        val cartItem2 = CartItem()
        cartItem2.uid = "1"
        cartItem2.foodId = "foodId2"
        cartItem2.foodSize = "Small"
        cartItem2.foodAddon = "Cheese"

        dao.insertOrReplaceAll(cartItem1, cartItem2).blockingAwait()

        dao.getItemWithAllOptionsInCart(cartItem2.uid, cartItem2.foodId, cartItem2.foodSize, cartItem2.foodAddon).test()
            .assertNoErrors()
            .assertValue {
                it == cartItem2
            }
    }

    @Test
    fun countCartItems() {
        val cartItem1 = CartItem()
        cartItem1.uid = "1"
        cartItem1.foodId = "foodId1"
        cartItem1.foodSize = "Large"
        cartItem1.foodAddon = "Rice"
        cartItem1.foodQuantity = 2

        val cartItem2 = CartItem()
        cartItem2.uid = "1"
        cartItem2.foodId = "foodId2"
        cartItem2.foodSize = "Small"
        cartItem2.foodAddon = "Cheese"
        cartItem2.foodQuantity = 3

        dao.insertOrReplaceAll(cartItem1, cartItem2).blockingAwait()

        dao.countCartItems(cartItem1.uid).test()
            .assertNoErrors()
            .assertValue {
                it == 5
            }
    }

    @Test
    fun totalPrice() {
        val cartItem1 = CartItem()
        cartItem1.uid = "1"
        cartItem1.foodId = "foodId1"
        cartItem1.foodSize = "Large"
        cartItem1.foodAddon = "Rice"
        cartItem1.foodQuantity = 2
        cartItem1.foodPrice = 5.0
        cartItem1.foodExtraPrice = 1.0

        val cartItem2 = CartItem()
        cartItem2.uid = "1"
        cartItem2.foodId = "foodId2"
        cartItem2.foodSize = "Small"
        cartItem2.foodAddon = "Cheese"
        cartItem2.foodQuantity = 3
        cartItem2.foodPrice = 2.0
        cartItem2.foodExtraPrice = 2.0

        dao.insertOrReplaceAll(cartItem1, cartItem2).blockingAwait()

        dao.totalPrice(cartItem1.uid).test()
            .assertNoErrors()
            .assertValue {
                it == 24.0
            }
    }

    @Test
    fun deleteCartItem() {
        val cartItem = CartItem()
        cartItem.uid = "1"

        dao.insertOrReplaceAll(cartItem).blockingAwait()
        val cartItemList = ArrayList<CartItem>()

        dao.getAllCart(cartItem.uid).subscribe({
            dao.deleteCart(cartItem).blockingGet()
        }, {throwable -> throwable.printStackTrace() })

        dao.getAllCart(cartItem.uid).test()
            .assertNoErrors()
            .assertValues(cartItemList)
    }

    @Test
    fun cleanCart() {
        val cartItem1 = CartItem()
        cartItem1.uid = "1"
        cartItem1.foodId = "foodId1"
        cartItem1.foodSize = "Large"
        cartItem1.foodAddon = "Rice"

        val cartItem2 = CartItem()
        cartItem2.uid = "1"
        cartItem2.foodId = "foodId2"
        cartItem2.foodSize = "Small"
        cartItem2.foodAddon = "Cheese"

        dao.insertOrReplaceAll(cartItem1, cartItem2).blockingAwait()
        dao.cleanCart(cartItem1.uid).blockingGet()

        dao.getAllCart(cartItem1.uid).test()
            .assertNoErrors()
            .assertValue {
                it.isEmpty()
            }
    }
}