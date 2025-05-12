package com.app.new_weather

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.app.new_weather.GetLocation.Companion.callback
import com.app.new_weather.GetLocation.Companion.fusedClient
import com.app.new_weather.GetLocation.Companion.locRequest
import com.app.new_weather.LatLong.Companion.latitude
import com.app.new_weather.LatLong.Companion.longitude
import com.app.new_weather.LatLong.Companion.place
import com.app.new_weather.LatLong.Companion.post_code
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import java.util.Locale

// Just let me use the inbuilt GPS tracker, Pichai does not need to know where I am.
class GetLocation {

    companion object {
        lateinit var fusedClient: FusedLocationProviderClient
        lateinit var callback: LocationCallback
        lateinit var locRequest: LocationRequest
    }
}

@SuppressLint("MissingPermission")
fun get_location(context: Context) {

    callback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            for (l in locationResult.locations) {
                if (l != null) return
            }
        }
    }

    locRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 300000)
        .setWaitForAccurateLocation(true)
        .build()

    fusedClient = LocationServices.getFusedLocationProviderClient(context) //this is where the location is corrected for the first time.
    fusedClient.lastLocation
        .addOnSuccessListener { l: Location? ->
            if (l != null) {
                latitude = l.latitude
                longitude = l.longitude
            }
        }


    fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY , object : CancellationToken() {
        override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token
        override fun isCancellationRequested() = false
    })
        .addOnSuccessListener { l: Location? ->
            if (l != null) {
                latitude = l.latitude
                longitude = l.longitude
            }
        }
}

@SuppressLint("MissingPermission")
fun startLocationUpates(context: Context) {
    fusedClient.requestLocationUpdates(locRequest, callback, Looper.getMainLooper())
    fusedClient.lastLocation
        .addOnSuccessListener { l: Location? ->
            if (l != null) {
                latitude = l.latitude
                longitude = l.longitude
                api_location = "${latitude},${longitude}"

                use_lat_and_long(context)
                getJsonFromAPI(api_location, context as MainActivity)
            }
        }
}

fun use_lat_and_long(context: Context) {
    var city: String?
    var town: String?
    var street: String?   
    val geo = Geocoder(context, Locale.getDefault())

    geo.getFromLocation(latitude, longitude, 1)
    { address_info ->
        city = address_info[0].locality
        town = address_info[0].subLocality
        street = address_info[0].thoroughfare
        post_code = address_info[0].postalCode
        place = "$number $street"

        if (number == null) {
            place = street
            if (street == null) {
                place = town
                if (town == null) {
                    place = city
                    if (city == null) {
                        place = post_code
                    }
                }
            }
        }
    }
}

