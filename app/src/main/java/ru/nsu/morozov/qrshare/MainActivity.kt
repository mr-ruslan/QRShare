package ru.nsu.morozov.qrshare

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.nsu.morozov.qrshare.databinding.ActivityMainBinding
import ru.nsu.morozov.qrshare.ui.share.ShareFragment
import ru.nsu.morozov.qrshare.ui.share.ShareFragmentArgs


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_get,
                R.id.navigation_share,
                R.id.navigation_history
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setOnItemSelectedListener { item ->
            NavigationUI.onNavDestinationSelected(item, navController)

            return@setOnItemSelectedListener true
        }
        if (savedInstanceState == null) {
            when (intent?.action) {
                Intent.ACTION_SEND -> {
                    if ("text/plain" == intent.type) {
                        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                            handleSendText(it, findNavController(R.id.nav_host_fragment_activity_main))
                        } // Handle text being sent
                    }
                }

                Intent.ACTION_VIEW -> {
                    val deeplink = intent.data
                    if (deeplink != null) {
                        deeplink.getQueryParameter("share_id")
                            ?.let {
                                startFromLink(it, findNavController(R.id.nav_host_fragment_activity_main))
                            }

                    }
                }

                else -> {
                    // Handle other intents, such as being started from the home screen
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    private fun startFromLink(uri: String, navController: NavController) {
        val navView: BottomNavigationView = binding.navView
        navView.selectedItemId = R.id.navigation_share
        var bundle = ShareFragmentArgs.Builder().setShareId(uri).build().toBundle()
        navController.navigate(R.id.navigation_share, bundle)

    }

    private fun handleSendText(data: String, navController: NavController) {
        val navView: BottomNavigationView = binding.navView
        navView.selectedItemId = R.id.navigation_share
        var bundle = ShareFragmentArgs.Builder().setShareData(data).build().toBundle()
        navController.navigate(R.id.navigation_share, bundle)
    }

    override fun onNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment_activity_main).navigateUp()
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment_activity_main).navigateUp()
    }

    fun getBottomNavView(): BottomNavigationView {
        return binding.navView
    }

}