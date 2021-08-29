package io.github.jwgibanez.cartrack.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.jwgibanez.cartrack.TestUtil
import io.github.jwgibanez.cartrack.data.model.Account
import io.github.jwgibanez.cartrack.data.model.User
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var accountDao: AccountDao
    private lateinit var userDao: UserDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        accountDao = db.accountDao()
        userDao = db.userDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeAccountAndRead() {
        val account: Account = TestUtil.createAccount(
            "admin", "pass1234", "Singapore"
        )
        accountDao.insertAll(account)
        val fromDb = accountDao.find("admin")
        assertThat(fromDb, equalTo(account))
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndRead() {
        val user: User = TestUtil.createUser(1).apply {
            name = "Leanne Graham"
            username = "Bret"
            email = "Sincere@april.biz"
            address = TestUtil.createAddress().apply {
                street = "Kulas Light"
                suite = "Apt. 556"
                city = "Gwenborough"
                zipcode = "92998-3874"
                geo = TestUtil.createGeo().apply {
                    lat = "-37.3159"
                    lng = "81.1496"
                }
            }
            phone = "1-770-736-8031 x56442"
            website = "hildegard.org"
            company = TestUtil.createCompany().apply {
                name = "Romaguera-Crona"
                catchPhrase = "Multi-layered client-server neural-net"
                bs = "harness real-time e-markets"
            }
        }
        userDao.insertAll(user)
        val fromDb = userDao.find(1)
        assertThat(fromDb, equalTo(user))
    }
}