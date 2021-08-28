package io.github.jwgibanez.cartrack.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.jwgibanez.cartrack.data.model.User

@Dao
interface UserDao {

    @Query("SELECT * FROM user ORDER BY id ASC")
    fun all(): LiveData<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(users: List<User>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg user: User)

    @Query("DELETE FROM user WHERE id = :id")
    fun delete(id: String)
}