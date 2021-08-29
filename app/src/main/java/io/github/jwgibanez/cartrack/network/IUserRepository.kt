package io.github.jwgibanez.cartrack.network

import android.app.Activity

interface IUserRepository {

    suspend fun fetchUsers(
        activity: Activity?,
        started: () -> Unit,
        error: (Exception) -> Unit,
        completed: () -> Unit
    )

    fun isNetworkConnected(
        activity: Activity,
        connected : () -> Unit,
        error: (e: String) -> Unit
    )
}