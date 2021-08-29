package io.github.jwgibanez.cartrack.data

import androidx.lifecycle.LiveData
import io.github.jwgibanez.cartrack.data.model.Account

class LoginRepository(private val dataSource: LoginDataSource) {

    var account: LiveData<Account?> = dataSource.appDb.accountDao().loggedInUser()
    var rememberedAccount: LiveData<Account?> = dataSource.appDb.accountDao().rememberedUser()

    val isLoggedIn: Boolean
        get() = account.value != null

    suspend fun logout() = account.value?.let { dataSource.logout(it) }

    suspend fun login(
        username: String,
        password: String,
        country: String,
        shouldRemember: Boolean
    ) = dataSource.login(username, password, country, shouldRemember)
}