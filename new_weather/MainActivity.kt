package com.app.new_weather

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.ACCESS_NETWORK_STATE
import android.Manifest.permission.INTERNET
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.app.new_weather.ui.theme.New_WeatherTheme
import kotlinx.coroutines.launch

interface ApiResponse {
    fun feed_success()
    fun feed_failure(e: Exception?)
}

open class MainActivity :  AppCompatActivity(), ApiResponse {
    private val REQ_CODE = 111
    private lateinit var dialog: AlertDialog
    private val viewModel: MainViewModel by viewModels()

    override fun onStart() {super.onStart()}

    override fun onPause() {super.onPause()}

    override fun onResume() {super.onResume()}

    override fun onStop() {super.onStop()}

    override fun onDestroy() {super.onDestroy()}

    override fun onRestart() {super.onRestart()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val compose_view = findViewById<ComposeView>(R.id.compose_view) //to get my toolbar in

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar

        requestPermissions()
        lifecycleScope.launch {
            get_location(this@MainActivity)
        }

        dialog = AlertDialog.Builder(this).create()
        dialog.setCancelable(false)
        dialog.setMessage("Give me a few secs...\n  (or the api may be offline)")
        if (!viewModel.showSuccessScreen.value) {
            dialog.show()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.weatherUiState.collect { uiState ->
                        if (viewModel.showSuccessScreen.value) {
                            if (dialog.isShowing) { dialog.dismiss() }
                            compose_view.setContent {
                                New_WeatherTheme {
                                    Surface(
                                        modifier = Modifier.fillMaxSize(),
                                        color = MaterialTheme.colorScheme.background
                                    ) {
                                        setComposableContent(uiState)
                                    }
                                }
                            }
                        }
                    }
                }
                launch {
                    viewModel.showSuccessScreen.collect { showScreen ->
                        if (showScreen) {
                            if (dialog.isShowing) {
                                dialog.dismiss()
                            }
                        }
                    }
                }

                launch {
                    viewModel.errorMessage.collect { errorMessage ->
                        if (errorMessage != null) {
                            if (dialog.isShowing) {
                                dialog.dismiss()
                            }
                            Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            }
        }
    }

    override fun feed_success() {
        runOnUiThread {
            if (::dialog.isInitialized) {
                dialog.dismiss()
            }
            viewModel.onSuccess()
        }
    }

    override fun feed_failure(e: Exception?) {
        runOnUiThread {
            if (::dialog.isInitialized) {
                dialog.dismiss()
            }
            viewModel.onFailure(e)
        }
    }

    private fun requestPermissions() {
        //Here be your permissions request, everybody else gets suppressed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val cluckup1 = checkSelfPermission(ACCESS_FINE_LOCATION)
            if (cluckup1 != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(
                        ACCESS_FINE_LOCATION,
                        ACCESS_COARSE_LOCATION,
                        INTERNET,
                        ACCESS_NETWORK_STATE
                    ),
                    REQ_CODE
                )
            }
        }
    }


    //The toolbar and menu stuff
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.aboutmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.itemId
        when (item.itemId) {
            R.id.aboutBox -> {
                val a = Intent(this, About::class.java)
                a.putExtra("title", "Your weather, sort of")
                a.putExtra(
                    "body",
                    "The weather where you are, with some graphics from www.deviantart.com/vclouds,"
                            + "the responses are from weatherapi.com (Which is only three days, including today)\n"
                            + "\nThis is just a test peice that takes your location from the gps and uses it to "
                            + "query the weather api. Rotate or swipe to reload.\n"
                )
                startActivity(a)
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
        return true
    }
}








