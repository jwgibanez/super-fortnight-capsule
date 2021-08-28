package io.github.jwgibanez.cartrack.network

import io.github.jwgibanez.cartrack.data.model.User
import io.reactivex.Observable
import retrofit2.http.GET

interface UserService {
    @GET("users")
    fun getUsers() : Observable<List<User>>

    companion object {
        const val API_HOST = "https://jsonplaceholder.typicode.com/"
    }
}