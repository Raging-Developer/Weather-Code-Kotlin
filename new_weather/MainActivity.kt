package com.app.new_weather

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.ACCESS_NETWORK_STATE
import android.Manifest.permission.INTERNET
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.app.new_weather.data.Current
import com.app.new_weather.data.Forecast
import com.app.new_weather.GetLocation.Companion.callback
import com.app.new_weather.GetLocation.Companion.fusedClient
import com.app.new_weather.GetLocation.Companion.locRequest
import com.app.new_weather.LatLong.Companion.astro
import com.app.new_weather.LatLong.Companion.chill
import com.app.new_weather.LatLong.Companion.cond
import com.app.new_weather.LatLong.Companion.cond_text
import com.app.new_weather.LatLong.Companion.forecast_day
import com.app.new_weather.LatLong.Companion.icon
import com.app.new_weather.LatLong.Companion.latitude
import com.app.new_weather.LatLong.Companion.longitude
import com.app.new_weather.LatLong.Companion.temp
import com.app.new_weather.LatLong.Companion.wind_dir
import com.app.new_weather.LatLong.Companion.wind_mph
import com.app.new_weather.ui.theme.New_WeatherTheme
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {
    private val REQ_CODE = 111

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedClient = LocationServices.getFusedLocationProviderClient(this)

        //Here be your permissions request, everybody else gets suppressed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val cluckup1 = checkSelfPermission(ACCESS_FINE_LOCATION)
            if (cluckup1 != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(
                        ACCESS_FINE_LOCATION,
                        ACCESS_COARSE_LOCATION,
                        INTERNET,
                        ACCESS_NETWORK_STATE),
                    REQ_CODE)
            }
        }

        get_location(this)
        use_lat_and_long(this)
        val api_location = "${latitude},${longitude}"
        getJsonFromAPI(api_location, this)
        fusedClient.requestLocationUpdates(locRequest, callback, Looper.getMainLooper())

        setContent {
            New_WeatherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background) {
                        BackGroundImage(this)
                }
            }
        }
    }

    override fun onStart(){super.onStart()}
    override fun onPause(){super.onPause()}

    override fun onResume() {
        super.onResume()
        startLocationUpates()
    }

    override fun onStop(){super.onStop()}
    override fun onDestroy(){super.onDestroy()}
    override fun onRestart(){super.onRestart()}

    @SuppressLint("MissingPermission")
    private fun startLocationUpates() {
        fusedClient.requestLocationUpdates(locRequest, callback, Looper.getMainLooper())
    }

    fun feed_success(current: Current, forc: Forecast) {
        cond = current.getCondition()
        astro = forc.getAstro()
        temp = current.getTemp_c().toString()
        chill = current.getFeelslike_c().toString()
        cond_text = current.getCondition()?.getText().toString()
        icon = current.getCondition()?.getIcon().toString()
        forecast_day = forc.getForecast_day()
        wind_mph = current.getWind_mph()
        wind_dir = current.getWind_dir()
    }

    fun feed_failure(e: Exception?) {
        Toast.makeText(this, "Ooops... " + e?.message, Toast.LENGTH_LONG)
            .show()
    }
}


