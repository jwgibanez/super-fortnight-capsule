package io.github.jwgibanez.cartrack.data

import io.github.jwgibanez.cartrack.data.db.AppDatabase
import io.github.jwgibanez.cartrack.data.model.Account
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource(val appDb: AppDatabase) {

    suspend fun login(username: String, password: String): Result<Account> =
        try {
            withContext(Dispatchers.IO) {
                appDb.accountDao().find(username, password).let {
                    if (it != null) {
                        Result.Success(it)
                    } else {
                        Result.Error(Exception("No such username"))
                    }
                }
            }
        } catch (e: Throwable) {
            Result.Error(Exception("Error logging in", e))
        }

    suspend fun logout() {
        // TODO: revoke authentication
    }
}