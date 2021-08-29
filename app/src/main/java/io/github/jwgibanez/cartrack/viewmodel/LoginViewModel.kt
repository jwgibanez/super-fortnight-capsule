package io.github.jwgibanez.cartrack.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jwgibanez.cartrack.data.LoginRepository
import io.github.jwgibanez.cartrack.data.Result

import io.github.jwgibanez.cartrack.R
import io.github.jwgibanez.cartrack.data.db.AppDatabase
import io.github.jwgibanez.cartrack.data.model.User
import io.github.jwgibanez.cartrack.network.UserRepository
import io.github.jwgibanez.cartrack.view.login.LoggedInUserView
import io.github.jwgibanez.cartrack.view.login.LoginFormState
import io.github.jwgibanez.cartrack.view.login.LoginResult
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    appDatabase: AppDatabase,
    private val loginRepository: LoginRepository,
    private val userRepository: UserRepository
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
        shouldRemember: Boolean
    ) {
        viewModelScope.launch {
            // can be launched in a separate asynchronous job
            val result = loginRepository.login(username, password, shouldRemember)

            if (result is Result.Success) {
                _loginResult.value =
                    LoginResult(success = LoggedInUserView(message = "Successful login"))
            } else {
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
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

    fun fetchUsers(activity: Activity) {
        viewModelScope.launch {
            userRepository.fetchUsers(
                activity,
                { /* started: do nothing */ },
                { /* error: do nothing */ },
                { /* completed: do nothing */ }
            )
        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return username.isNotBlank()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}