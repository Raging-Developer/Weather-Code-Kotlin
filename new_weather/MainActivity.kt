package com.app.new_weather

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.ACCESS_NETWORK_STATE
import android.Manifest.permission.INTERNET
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
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
import com.app.new_weather.GetLocation.Companion.callback
import com.app.new_weather.GetLocation.Companion.fusedClient
import com.app.new_weather.GetLocation.Companion.locRequest
import com.app.new_weather.LatLong.Companion.api_location
import com.app.new_weather.LatLong.Companion.latitude
import com.app.new_weather.LatLong.Companion.longitude
import com.app.new_weather.data.Current
import com.app.new_weather.data.Forecast
import com.app.new_weather.ui.theme.New_WeatherTheme

class MainActivity : AppCompatActivity() {
    private val REQ_CODE = 111
    private lateinit var dialog: AlertDialog

    override fun onStart(){super.onStart()}
    override fun onPause(){super.onPause()}

    override fun onResume() {
        super.onResume()
        startLocationUpates()
    }

    override fun onStop(){super.onStop()}
    override fun onDestroy(){super.onDestroy()}
    override fun onRestart(){super.onRestart()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar


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

        dialog = AlertDialog.Builder(this).create()
        dialog.setMessage("Give me a few secs...")
        dialog.show()

        get_location(this)
        startLocationUpates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpates() {
        fusedClient.requestLocationUpdates(locRequest, callback, Looper.getMainLooper())
        fusedClient.lastLocation
        .addOnSuccessListener { l: Location? ->
            if (l != null) {
                latitude = l.latitude
                longitude = l.longitude
                api_location = "${latitude},${longitude}"

                use_lat_and_long(this)
                getJsonFromAPI(api_location, this)
            }
        }
    }

    //Load up the viewModel using a Factory
    fun feed_success(current: Current, forc: Forecast) {
        dialog.dismiss()
        
        val compose_view = ComposeView(this)
        //Now the only call to setContent, via a findViewById which google doesn't mention
        findViewById<ComposeView>(R.id.compose_view)
            .setContent {
                New_WeatherTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        setComposableContent( compose_view)
                    }
                }
            }
    }

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
                    "The weather where you are, with some graphics from http://vclouds.deviantart,"
                            + "the responses are from weatherapi.com (Which is only three days, including today)\n"
                            + "\nThis is just a test peice that takes your location from the gps and uses it to "
                            + "query the weather api. Rotating your device will cause it to reload.\n"
                )
                startActivity(a)
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
        return false
    }

    fun feed_failure(e: Exception?) {
        dialog.dismiss()
        Toast.makeText(this, "Ooops... " + e?.message, Toast.LENGTH_LONG)
        .show()
    }
}
