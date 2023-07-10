package com.example.todoapp.ui.activity

import android.app.Dialog
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.todoapp.R
import com.example.todoapp.ToDoApplication
import com.example.todoapp.data.network.workers.ServerUpdateWorker
import com.example.todoapp.databinding.ActivityMainBinding
import com.example.todoapp.di.components.ActivityComponent
import com.example.todoapp.ui.fragments.SettingsFragment
import com.google.android.material.navigation.NavigationView
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var activityComponent: ActivityComponent
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent = (applicationContext as ToDoApplication).applicationComponent.activityComponent()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        setSupportActionBar(binding.appBarMain.toolbar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val periodicWorkRequest = PeriodicWorkRequestBuilder<ServerUpdateWorker>(8, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(this).enqueue(periodicWorkRequest)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_settings) {
                val bottomSheetFragment = SettingsFragment()
                bottomSheetFragment.show(supportFragmentManager, "bottomSheet")
                true
            } else false
        }
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("notification_permission", false)
        editor.apply()
        if (!sharedPreferences.getBoolean("notification_permission", false)) {
            showPermissionWindow()
        }
    }

    private fun showPermissionWindow() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.permission_layout)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lottieAnimationView : LottieAnimationView =
            dialog.findViewById<LottieAnimationView>(R.id.notificationAnim)
        lottieAnimationView.visibility = View.VISIBLE
        lottieAnimationView.repeatCount = LottieDrawable.INFINITE
        lottieAnimationView.playAnimation()
        dialog.setCancelable(false)
        val btnAllow = dialog.findViewById<Button>(R.id.btnAllow)
        val btnDeny = dialog.findViewById<Button>(R.id.btnDeny)
        btnAllow.setOnClickListener {
            savePermission(true)
            dialog.dismiss()
        }
        btnDeny.setOnClickListener {
            savePermission(false)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun savePermission(allowed: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("notification_permission", allowed)
        editor.apply()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun party() {
        val lottieAnimationView : LottieAnimationView = findViewById(R.id.party_animation)
        lottieAnimationView.visibility = View.VISIBLE
        lottieAnimationView.playAnimation()
        Handler().postDelayed({
            lottieAnimationView.visibility = View.GONE
        }, 2000)
    }
}