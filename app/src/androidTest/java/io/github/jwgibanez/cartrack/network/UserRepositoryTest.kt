package io.github.jwgibanez.cartrack.network

import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class UserRepositoryTest {

    private lateinit var repository: IUserRepository

    @Before
    fun before() {
        val service = FakeUserService()
        repository = FakeUserRepository(service)
    }

    @Test
    fun fetchUsersTest() {
        var started = false
        var hasError = false
        var completed = false
        runBlocking {
            repository.fetchUsers(null, {
                started = true
            }, {
                hasError = true
            }, {
                completed = true
            })
        }
        assertThat("Has started", started)
        assertThat("Has completed", completed)
        assertThat("Has no error", !hasError)
    }

}