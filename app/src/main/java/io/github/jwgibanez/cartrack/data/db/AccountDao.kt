package io.github.jwgibanez.cartrack.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.jwgibanez.cartrack.data.model.Account

@Dao
interface AccountDao {
    @Query("SELECT * FROM account ORDER BY username ASC")
    fun all(): LiveData<List<Account>>

    @Query("SELECT * FROM account WHERE username = :username AND password = :password")
    fun find(username: String, password: String): Account?

    @Query("SELECT * FROM account WHERE username = :username")
    fun find(username: String): Account?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(accounts: List<Account>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg account: Account)

    @Query("DELETE FROM account WHERE username = :username")
    fun delete(username: String)
}