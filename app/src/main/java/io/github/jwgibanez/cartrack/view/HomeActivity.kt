package io.github.jwgibanez.cartrack.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.jwgibanez.cartrack.R
import io.github.jwgibanez.cartrack.databinding.ActivityHomeBinding
import io.github.jwgibanez.cartrack.view.login.LoginFragmentDirections
import io.github.jwgibanez.cartrack.viewmodel.LoginViewModel

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Top-level destinations; hides back arrow
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.loginFragment,
                R.id.listFragment
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        // Try to fetch users in the background
        viewModel.fetchUsers(this)

        // If an account has previously logged-in, skip login form
        viewModel.loggedInAccount.observe(this) {
            if (it?.isLoggedIn == true
                && navController.currentDestination?.id  == R.id.loginFragment) {
                val action = LoginFragmentDirections.actionLoginFragmentToListFragment(it.username)
                navController.navigate(action)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}