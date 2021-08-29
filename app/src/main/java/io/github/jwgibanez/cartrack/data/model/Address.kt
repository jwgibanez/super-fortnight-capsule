package io.github.jwgibanez.cartrack.data.model

import java.io.Serializable

data class Address(
    var street: String? = null,
    var suite: String? = null,
    var city: String? = null,
    var zipcode: String? = null,
    var geo: Geo? = null
) : Serializable