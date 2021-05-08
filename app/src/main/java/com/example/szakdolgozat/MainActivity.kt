package com.example.szakdolgozat

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.szakdolgozat.repository.AuthRepository
import com.example.szakdolgozat.util.TitlebarTitleProvider
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val authRepo = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.my_toolbar))

        val navController = findNavController(R.id.nav_host_fragment)
        val bottomNavigationView =
            findViewById<BottomNavigationView>(R.id.activity_main_bottom_navigation_view)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        authRepo.user.observe(this) {
            navController.navigate(
                when (it) {
                    null -> R.id.loginFragment
                    else -> R.id.bottom_nav_dashboard
                }
            )
            bottomNavigationView.visibility = when (it) {
                null -> GONE
                else -> VISIBLE
            }
            val optionsMenu = findViewById<Toolbar>(R.id.my_toolbar).menu
            val settingsMenuItem = optionsMenu.findItem(R.id.action_settings)
            settingsMenuItem?.let { menuItem ->
                val userPresent = when (it) {
                    null -> false
                    else -> true
                }
                settingsMenuItem.setVisible(userPresent)
                settingsMenuItem.setEnabled(userPresent)
            }
        }

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            supportActionBar?.title = TitlebarTitleProvider.getTitle(destination.id)
            supportActionBar?.setDisplayHomeAsUpEnabled(
                when (destination.id) {
                    R.id.bottom_nav_settings -> true
                    else -> false
                }
            )
            bottomNavigationView.visibility = when (destination.id) {
                R.id.bottom_nav_settings -> GONE
                else -> VISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_toolbar, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.bottom_nav_settings)
            }
            android.R.id.home -> {
                findNavController(R.id.nav_host_fragment).navigateUp()
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
        return true
    }
}