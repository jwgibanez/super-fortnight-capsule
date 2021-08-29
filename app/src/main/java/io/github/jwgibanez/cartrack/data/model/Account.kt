package io.github.jwgibanez.cartrack.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account")
data class Account(
    @PrimaryKey var username: String,
    var password: String,
    var country: String,
    var isLoggedIn: Boolean = false,
    var isRemembered: Boolean = false
)