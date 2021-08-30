package io.github.jwgibanez.cartrack.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.jwgibanez.cartrack.data.LoginDataSource
import io.github.jwgibanez.cartrack.data.LoginRepository
import io.github.jwgibanez.cartrack.data.db.AccountDao
import io.github.jwgibanez.cartrack.data.db.AppDatabase
import io.github.jwgibanez.cartrack.data.model.Account
import io.github.jwgibanez.cartrack.network.FakeUserRepository
import io.github.jwgibanez.cartrack.network.FakeUserService
import io.github.jwgibanez.cartrack.view.login.LoginFormState
import io.github.jwgibanez.cartrack.view.login.LoginResult
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4::class)
class LoginViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var accountDao: AccountDao
    private lateinit var db: AppDatabase
    private lateinit var viewModel: LoginViewModel

    @Before
    fun before() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        accountDao = db.accountDao()
        val dataSource = LoginDataSource(db)
        val loginRepository = LoginRepository(dataSource)
        val userRepository = FakeUserRepository(FakeUserService())
        viewModel = LoginViewModel(db, loginRepository, userRepository)

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
    fun loginFails() {
        var loggedIn: Account? = null
        val observer0 = Observer<Account?> {
            loggedIn = it
        }
        var loginResult: LoginResult? = null
        val observer1 = Observer<LoginResult?> {
            loginResult = it
        }
        try {
            viewModel.loggedInAccount.observeForever(observer0)
            viewModel.loginResult.observeForever(observer1)
            runBlocking {
                viewModel.login(
                    username = "admin",
                    password = "wrongPass",
                    country = "Singapore",
                    shouldRemember = false
                )
            }
            sleep(100) // wait a bit for observer1 to complete
            MatcherAssert.assertThat("Account is null", loggedIn == null)
            MatcherAssert.assertThat("Result is failed", loginResult!!.error!! > 0 )
        } finally {
            viewModel.loggedInAccount.removeObserver(observer0)
            viewModel.loginResult.removeObserver(observer1)
        }
    }

    @Test
    @Throws(IOException::class)
    fun loginSuccessfully() {
        var loggedIn: Account? = null
        val observer0 = Observer<Account?> {
            loggedIn = it
        }
        var loginResult: LoginResult? = null
        val observer1 = Observer<LoginResult?> {
            loginResult = it
        }
        try {
            viewModel.loggedInAccount.observeForever(observer0)
            viewModel.loginResult.observeForever(observer1)
            runBlocking {
                viewModel.login(
                    username = "admin",
                    password = "pass1234",
                    country = "Singapore",
                    shouldRemember = false
                )
            }
            sleep(100) // wait a bit for observer1 to complete
            MatcherAssert.assertThat("Observer worked", loggedIn!!.username == "admin")
            MatcherAssert.assertThat("Results is success", loginResult!!.success!!.message == "Successful login" )
        } finally {
            viewModel.loggedInAccount.removeObserver(observer0)
            viewModel.loginResult.removeObserver(observer1)
        }
    }

    @Test
    @Throws(IOException::class)
    fun loginAndLogoutSuccessfully() {
        var loggedIn: Account? = null
        val observer0 = Observer<Account?> {
            loggedIn = it
        }
        var loginResult: LoginResult? = null
        val observer1 = Observer<LoginResult?> {
            loginResult = it
        }
        try {
            viewModel.loggedInAccount.observeForever(observer0)
            viewModel.loginResult.observeForever(observer1)
            runBlocking {
                viewModel.login(
                    username = "admin",
                    password = "pass1234",
                    country = "Singapore",
                    shouldRemember = false
                )
            }
            sleep(100) // wait a bit for observer1 to complete
            MatcherAssert.assertThat("Observer worked", loggedIn!!.username == "admin")
            MatcherAssert.assertThat("Results is success", loginResult!!.success!!.message == "Successful login" )
            // Logout
            runBlocking {
                viewModel.logout()
            }
            sleep(100) // wait a bit for db to update
            loggedIn = accountDao.loggedInAccount2()
            MatcherAssert.assertThat("Account is null", loggedIn == null)
        } finally {
            viewModel.loggedInAccount.removeObserver(observer0)
            viewModel.loginResult.removeObserver(observer1)
        }
    }

    @Test
    fun formValid() {
        var formState: LoginFormState? = null
        val observer1 = Observer<LoginFormState?> {
            formState = it
        }
        try {
            viewModel.loginFormState.observeForever(observer1)
            val context = ApplicationProvider.getApplicationContext<Context>()
            runBlocking {
                viewModel.loginDataChanged(
                    context = context,
                    username = "admin",
                    password = "pass1234",
                    country = "Singapore",
                )
            }
            sleep(100) // wait a bit for observer1 to complete
            MatcherAssert.assertThat("Form is valid", formState!!.isDataValid)
        } finally {
            viewModel.loginFormState.removeObserver(observer1)
        }
    }

    @Test
    fun formInValid_usernameBlank() {
        var formState: LoginFormState? = null
        val observer1 = Observer<LoginFormState?> {
            formState = it
        }
        try {
            viewModel.loginFormState.observeForever(observer1)
            val context = ApplicationProvider.getApplicationContext<Context>()
            runBlocking {
                viewModel.loginDataChanged(
                    context = context,
                    username = "", // username blank
                    password = "pass1234",
                    country = "Singapore",
                )
            }
            sleep(100) // wait a bit for observer1 to complete
            MatcherAssert.assertThat("Form is invalid", !formState!!.isDataValid)
        } finally {
            viewModel.loginFormState.removeObserver(observer1)
        }
    }

    @Test
    fun formInValid_passwordShort() {
        var formState: LoginFormState? = null
        val observer1 = Observer<LoginFormState?> {
            formState = it
        }
        try {
            viewModel.loginFormState.observeForever(observer1)
            val context = ApplicationProvider.getApplicationContext<Context>()
            runBlocking {
                viewModel.loginDataChanged(
                    context = context,
                    username = "admin",
                    password = "1234", // short password
                    country = "Singapore",
                )
            }
            sleep(100) // wait a bit for observer1 to complete
            MatcherAssert.assertThat("Form is invalid", !formState!!.isDataValid)
        } finally {
            viewModel.loginFormState.removeObserver(observer1)
        }
    }

    @Test
    fun formInValid_countryInValid() {
        var formState: LoginFormState? = null
        val observer1 = Observer<LoginFormState?> {
            formState = it
        }
        try {
            viewModel.loginFormState.observeForever(observer1)
            val context = ApplicationProvider.getApplicationContext<Context>()
            runBlocking {
                viewModel.loginDataChanged(
                    context = context,
                    username = "admin",
                    password = "pass1234",
                    country = "Select Country", // country is not selected
                )
            }
            sleep(100) // wait a bit for observer1 to complete
            MatcherAssert.assertThat("Form is invalid", !formState!!.isDataValid)
        } finally {
            viewModel.loginFormState.removeObserver(observer1)
        }
    }
}