package io.github.jwgibanez.cartrack.network

import android.app.Activity

class FakeUserRepository(private val userService: UserService) : IUserRepository {

    override suspend fun fetchUsers(
        activity: Activity?,
        started: () -> Unit,
        error: (Exception) -> Unit,
        completed: () -> Unit
    ) = run {
        started()
        userService.getUsers()
        completed()
    }

    override fun isNetworkConnected(
        activity: Activity,
        connected: () -> Unit,
        error: (e: String) -> Unit
    ) = connected()
}