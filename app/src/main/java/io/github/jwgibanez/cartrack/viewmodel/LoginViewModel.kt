package io.github.jwgibanez.cartrack.viewmodel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jwgibanez.cartrack.data.LoginRepository
import io.github.jwgibanez.cartrack.data.Result

import io.github.jwgibanez.cartrack.R
import io.github.jwgibanez.cartrack.data.db.AppDatabase
import io.github.jwgibanez.cartrack.data.model.User
import io.github.jwgibanez.cartrack.network.IUserRepository
import io.github.jwgibanez.cartrack.view.login.LoggedInUserView
import io.github.jwgibanez.cartrack.view.login.LoginFormState
import io.github.jwgibanez.cartrack.view.login.LoginResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    appDatabase: AppDatabase,
    private val loginRepository: LoginRepository,
    private val userRepository: IUserRepository
) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult?>()
    val loginResult: LiveData<LoginResult?> = _loginResult

    val loggedInAccount get() = loginRepository.account
    val rememberedAccount get() = loginRepository.rememberedAccount

    val users: LiveData<List<User>> = appDatabase.userDao().all()

    fun login(
        username: String,
        password: String,
        country: String,
        shouldRemember: Boolean
    ) {
        viewModelScope.launch {
            // can be launched in a separate asynchronous job
            val result = loginRepository.login(username, password, country, shouldRemember)

            if (result is Result.Success) {
                _loginResult.value =
                    LoginResult(success = LoggedInUserView(message = "Successful login"))
            } else {
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
        }
    }

    fun loginDataChanged(context: Context, username: String, password: String, country: String) {
        if (!isUserNameValid(username) || !isPasswordValid(password)
            || !isCountryValid(context, country)) {
            val state = LoginFormState()
            if (!isUserNameValid(username)) {
                state.usernameError = R.string.invalid_username
            }
            if (!isPasswordValid(password)) {
                state.passwordError = R.string.invalid_password
            }
            if (!isCountryValid(context, country)) {
                state.countryError = R.string.invalid_country
            }
            _loginForm.value = state
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    fun logout() {
        _loginResult.value = null
        viewModelScope.launch {
            loginRepository.logout()
        }
    }

    fun fetchUsers(
        activity: Activity,
        started: () -> Unit,
        error: (Exception?) -> Unit,
        completed: () -> Unit
    ) {
        viewModelScope.launch {
            userRepository.fetchUsers(
                activity,
                { viewModelScope.launch(Dispatchers.Main) { started() } },
                { viewModelScope.launch(Dispatchers.Main) { error(it) } },
                { viewModelScope.launch(Dispatchers.Main) { completed() } }
            )
        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return username.isNotBlank()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun isCountryValid(context: Context, country: String): Boolean {
        return country != context.resources.getStringArray(R.array.countries_array)[0]
    }
}