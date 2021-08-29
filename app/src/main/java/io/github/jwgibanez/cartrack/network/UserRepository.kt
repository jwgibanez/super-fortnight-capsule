package io.github.jwgibanez.cartrack.network

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import io.github.jwgibanez.cartrack.R
import io.github.jwgibanez.cartrack.data.db.AppDatabase
import io.github.jwgibanez.cartrack.data.model.User
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userService: UserService): IUserRepository {

    override suspend fun fetchUsers(
        activity: Activity?,
        started: () -> Unit,
        error: (Exception) -> Unit,
        completed: () -> Unit
    ) {
        withContext(Dispatchers.IO) {
            isNetworkConnected(
                activity!!,
                {
                    userService.getUsers().safeSubscribe(object : Observer<List<User>> {
                        override fun onSubscribe(d: Disposable) {
                            started()
                        }

                        override fun onNext(value: List<User>) {
                            AppDatabase.ioThread {
                                val db  = AppDatabase.getInstance(activity)
                                db.userDao().insert(value)
                            }
                        }

                        override fun onError(e: Throwable) {
                            error(e)
                        }

                        override fun onComplete() {
                            completed()
                        }
                    })
                },
                { error -> error(Exception(error)) }
            )
        }
    }

    override fun isNetworkConnected(
        activity: Activity,
        connected : () -> Unit,
        error: (e: String) -> Unit
    ) {
        val cm =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected) {
            connected()
        } else {
            error(activity.getString(R.string.message_no_internet_connection))
        }
    }
}