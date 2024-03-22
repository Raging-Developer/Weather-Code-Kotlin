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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale

class GetLocation {

    companion object {
        lateinit var fusedClient: FusedLocationProviderClient
        lateinit var callback: LocationCallback
        lateinit var locRequest: LocationRequest

    }
}

@SuppressLint("MissingPermission")
fun get_location(context: Context) {
    fusedClient = LocationServices.getFusedLocationProviderClient(context)

    fusedClient.lastLocation
        .addOnSuccessListener { l: Location? ->
            if (l != null) {
                latitude = l.latitude
                longitude = l.longitude
            }
        }

    locRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 300000)
        .setWaitForAccurateLocation(false)
        .build()
    callback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (l in locationResult.locations) {
                if (l != null) {
                    latitude = l.latitude
                    longitude = l.longitude
                }
            }
        }
    }
}

fun use_lat_and_long(context: Context) {
    var city: String?
    var town: String?
    var street: String?
    var post_code: String?
    val geo = Geocoder(context, Locale.getDefault())

    geo.getFromLocation(latitude, longitude, 1)
    { address_info ->
        city = address_info[0].locality
        town = address_info[0].subLocality
        street = address_info[0].thoroughfare
        post_code = address_info[0].postalCode
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


