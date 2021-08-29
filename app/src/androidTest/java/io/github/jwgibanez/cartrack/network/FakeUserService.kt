package io.github.jwgibanez.cartrack.network

import io.github.jwgibanez.cartrack.TestUtil
import io.github.jwgibanez.cartrack.data.model.User
import io.reactivex.Observable

class FakeUserService : UserService {

    override fun getUsers(): Observable<List<User>> {
        return Observable.just(TestUtil.getUserList())
    }
}