package io.github.jwgibanez.cartrack.data

import io.github.jwgibanez.cartrack.data.db.AppDatabase
import io.github.jwgibanez.cartrack.data.model.Account
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource(val appDb: AppDatabase) {

    private val accountDao = appDb.accountDao()

    suspend fun login(
        username: String,
        password: String,
        country: String,
        shouldRemember: Boolean
    ): Result<Nothing> =
        try {
            withContext(Dispatchers.IO) {
                accountDao.find(username, password).let { account ->
                    if (account != null) {
                        account.isLoggedIn = true
                        account.isRemembered = shouldRemember
                        account.country = country
                        accountDao.forgetExcept(account.username)
                        accountDao.insertAll(account)
                        Result.Success(null)
                    } else {
                        Result.Error(Exception("No such username"))
                    }
                }
            }
        } catch (e: Throwable) {
            Result.Error(Exception("Error logging in", e))
        }

    suspend fun logout(account: Account) {
        withContext(Dispatchers.IO) {
            account.isLoggedIn = false
            accountDao.insertAll(account)
        }
    }
}