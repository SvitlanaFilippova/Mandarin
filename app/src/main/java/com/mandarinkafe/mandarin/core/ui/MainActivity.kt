package com.mandarinkafe.mandarin.core.ui

import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.cart.CartFragment
import com.mandarinkafe.mandarin.databinding.ActivityMainBinding
import com.mandarinkafe.mandarin.navigation.MainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = requireNotNull(_binding) { "Binding wasn't initialized" }
    private var _appBarConfiguration: AppBarConfiguration? = null
    private val appBarConfiguration: AppBarConfiguration
        get() = requireNotNull(_appBarConfiguration) { "App Bar Configuration wasn't initialized" }

    private var cartFragment: CartFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeUI()

        setContent {
            MainScreen(supportFragmentManager)
        }
    }

    private fun initializeUI() {
        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.container_view) as NavHostFragment? ?: return
        val navController = host.navController
        _appBarConfiguration = AppBarConfiguration(
            navController.graph
        )
        binding.bnvMain.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.appBarMain.btCart.setOnClickListener {
            navController.navigate(
                R.id.cartFragment
            )
        }

        val headToolbar = binding.appBarMain.toolbar

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val window = window
            when (destination.id) {
                R.id.mealDetails -> {
                    headToolbar.visibility = View.GONE
                    window.statusBarColor =
                        ContextCompat.getColor(this, R.color.light_status_bar_color)
                    //TODO deprecated. Пока не поняла, чем заменить.

                }

                else -> {
                    headToolbar.visibility = View.VISIBLE
                    window.statusBarColor =
                        ContextCompat.getColor(this, R.color.default_status_bar_color)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.container_view)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun updateCartAdapter() {
        cartFragment?.displayCartItems()
    }
}