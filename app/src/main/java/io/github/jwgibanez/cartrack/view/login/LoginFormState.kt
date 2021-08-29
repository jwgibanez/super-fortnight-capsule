package io.github.jwgibanez.cartrack.view.login

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    var usernameError: Int? = null,
    var passwordError: Int? = null,
    var countryError: Int? = null,
    var isDataValid: Boolean = false
)