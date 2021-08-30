package io.github.jwgibanez.cartrack.data

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.jwgibanez.cartrack.data.db.AccountDao
import io.github.jwgibanez.cartrack.data.db.AppDatabase
import io.github.jwgibanez.cartrack.data.model.Account
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class LoginRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var accountDao: AccountDao
    private lateinit var db: AppDatabase
    private lateinit var dataSource: LoginDataSource
    private lateinit var repository: LoginRepository

    @Before
    fun before() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        accountDao = db.accountDao()
        dataSource = LoginDataSource(db)
        repository = LoginRepository(dataSource)

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
        var loggedIn: Account? = null
        val observer = Observer<Account?> {
            loggedIn = it
        }
        try {
            repository.account.observeForever(observer)
            val result: Result<Nothing>
            runBlocking {
                result = repository.login(
                    username = "admin",
                    password = "pass1234",
                    country = "Singapore",
                    shouldRemember = false
                )
            }
            assertThat("Login is success", result is Result.Success)
            assertThat("Observer worked", loggedIn?.username == "admin")
            assertThat("Internal flag works", repository.isLoggedIn)
        } finally {
            repository.account.removeObserver(observer)
        }
    }

    @Test
    @Throws(IOException::class)
    fun loginAndLogoutSuccessfully() {
        var loggedIn: Account? = null
        val observer = Observer<Account?> {
            loggedIn = it
        }
        try {
            repository.account.observeForever(observer)
            val result: Result<Nothing>
            runBlocking {
                result = repository.login(
                    username = "admin",
                    password = "pass1234",
                    country = "Singapore",
                    shouldRemember = false
                )
            }
            assertThat("Login is success", result is Result.Success)
            assertThat("Observer login worked", loggedIn?.username == "admin")
            assertThat("Internal flag works", repository.isLoggedIn)
            runBlocking {
                repository.logout()
            }
            assertThat("Observer logout worked", loggedIn == null)
            assertThat("Internal flag works", !repository.isLoggedIn)
        } finally {
            repository.account.removeObserver(observer)
        }
    }
}