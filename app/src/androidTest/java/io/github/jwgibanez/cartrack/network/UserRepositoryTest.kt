package io.github.jwgibanez.cartrack.network
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert
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
        MatcherAssert.assertThat("Has started", started)
        MatcherAssert.assertThat("Has completed", completed)
        MatcherAssert.assertThat("Has no error", !hasError)
    }

}