package com.app.new_weather

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.app.new_weather.GetLocation.Companion.fusedClient
import com.app.new_weather.LatLong.Companion.api_location
import com.app.new_weather.LatLong.Companion.latitude
import com.app.new_weather.LatLong.Companion.longitude
import com.app.new_weather.LatLong.Companion.place
import com.app.new_weather.LatLong.Companion.post_code
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import java.util.Locale

data class GetLocation(var fusedClient: FusedLocationProviderClient) {
    companion object {
        lateinit var fusedClient: FusedLocationProviderClient
    }
}

@SuppressLint("MissingPermission")
internal fun get_location(context: Context) {
    fusedClient = LocationServices.getFusedLocationProviderClient(context)
    fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
        override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token
        override fun isCancellationRequested() = false
    })
        .addOnSuccessListener { l: Location? ->
            if (l != null) {
                latitude = l.latitude
                longitude = l.longitude
                api_location = "${latitude},${longitude}"
                useLocationUpates(context)
            }
        }
}

internal fun useLocationUpates(context: Context) {
    use_lat_and_long(context)
    getJsonFromAPI(api_location, context as MainActivity)
}

private fun use_lat_and_long(context: Context ) {
    var city: String?
    var town: String?
    var street: String?
    var number: String?
    val geo = Geocoder(context, Locale.getDefault())

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



