package io.github.jwgibanez.cartrack.data.model

import java.io.Serializable

data class Company(
    var name: String? = null,
    var catchPhrase: String? = null,
    var bs: String? = null
) : Serializable