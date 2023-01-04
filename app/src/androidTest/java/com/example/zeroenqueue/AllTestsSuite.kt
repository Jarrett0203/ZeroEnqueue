package com.example.zeroenqueue

import com.example.zeroenqueue.activity.LoginActivityTest
import com.example.zeroenqueue.activity.MainCustomerActivityTest
import com.example.zeroenqueue.activity.MainVendorActivityTest
import com.example.zeroenqueue.db.CartDAOTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.junit.runners.Suite

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(Suite::class)
@Suite.SuiteClasses(
    CartDAOTest::class,
    LoginActivityTest::class,
    MainVendorActivityTest::class,
    MainCustomerActivityTest::class
)
class AllTestsSuite