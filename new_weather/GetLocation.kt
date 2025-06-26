package com.app.new_weather

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Looper
import com.app.new_weather.GetLocation.Companion.callback
import com.app.new_weather.GetLocation.Companion.fusedClient
import com.app.new_weather.GetLocation.Companion.locRequest
import com.app.new_weather.LatLong.Companion.api_location
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
import java.util.Locale

data class GetLocation(var callback: LocationCallback) {
    companion object {
        lateinit var fusedClient: FusedLocationProviderClient
        lateinit var callback: LocationCallback
        lateinit var locRequest: LocationRequest
    }
}

@SuppressLint("MissingPermission")
fun get_location(context: Context) {
    fusedClient = LocationServices.getFusedLocationProviderClient(context)

    callback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            for (l in locationResult.locations) {
                if (l != null) {
                    latitude = l.latitude
                    longitude = l.longitude
                    api_location = "${latitude},${longitude}"
                    startLocationUpates(context)
                }
            }
        }
    }

    locRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 300000)
        .setWaitForAccurateLocation(true)
        .build()

    fusedClient.requestLocationUpdates(locRequest, callback, Looper.getMainLooper())
}

fun startLocationUpates(context: Context) {
    use_lat_and_long(context)
    getJsonFromAPI(api_location, context as MainActivity)
}

fun use_lat_and_long(context: Context ) {
    var city: String?
    var town: String?
    var street: String?
    var number: String?
    val geo = Geocoder(context, Locale.getDefault())

    //Well no shit goggle, you have deprecated it with no documentation, again!
    //This is meant to be deprecated and look like this with a listener all as parameters.
    geo.getFromLocation(latitude, longitude, 1)
    { address_info ->
        city = address_info[0].locality
        town = address_info[0].subLocality
        street = address_info[0].thoroughfare
        number = address_info[0].subThoroughfare
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

fun stopLocationUpdates(){
    fusedClient.removeLocationUpdates(callback)
}

