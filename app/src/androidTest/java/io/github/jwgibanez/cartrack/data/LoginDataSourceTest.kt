package io.github.jwgibanez.cartrack.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.jwgibanez.cartrack.data.db.AccountDao
import io.github.jwgibanez.cartrack.data.db.AppDatabase
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class LoginDataSourceTest {

    private lateinit var accountDao: AccountDao
    private lateinit var db: AppDatabase
    private lateinit var dataSource: LoginDataSource

    @Before
    fun before() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        accountDao = db.accountDao()
        dataSource = LoginDataSource(db)

        // pre-populate accounts
        accountDao.insert(AppDatabase.PREPOPULATE_DATA)
    }

    @After
    @Throws(IOException::class)
    fun after() {
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun loginSuccessfully() {
        val result: Result<Nothing>
        runBlocking {
            result = dataSource.login(
                username = "admin",
                password = "pass1234",
                country = "Singapore",
                shouldRemember = false
            )
        }
        assertThat("Login is success", result is Result.Success)
    }

    @Test
    @Throws(IOException::class)
    fun loginFailed_wrongUsername() {
        val result: Result<Nothing>
        runBlocking {
            result = dataSource.login(
                username = "notAUser",
                password = "pass1234",
                country = "Singapore",
                shouldRemember = false
            )
        }
        assertThat("Login is failing", result is Result.Error)
    }

    @Test
    @Throws(IOException::class)
    fun loginFailed_wrongPassword() {
        val result: Result<Nothing>
        runBlocking {
            result = dataSource.login(
                username = "admin",
                password = "wrong_password",
                country = "Singapore",
                shouldRemember = false
            )
        }
        assertThat("Login is failing", result is Result.Error)
    }

    @Test
    @Throws(IOException::class)
    fun loginAndLogout() {
        val result: Result<Nothing>
        runBlocking {
            result = dataSource.login(
                username = "admin",
                password = "pass1234",
                country = "Singapore",
                shouldRemember = false
            )
        }
        assertThat("Login is success 1", result is Result.Success)
        var loggedIn = accountDao.loggedInAccount2()
        assertThat("Login is success 2", loggedIn!!.isLoggedIn)
        runBlocking {
            dataSource.logout(loggedIn!!)
        }
        loggedIn = accountDao.find(loggedIn.username)
        assertThat("Logout is success 1", !loggedIn!!.isLoggedIn)
    }

}