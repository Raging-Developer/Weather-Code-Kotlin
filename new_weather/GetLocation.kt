package com.app.new_weather

import android.annotation.SuppressLint
import android.app.Activity
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class GetLocation(var fusedClient: FusedLocationProviderClient) {
    companion object {
        lateinit var fusedClient: FusedLocationProviderClient
    }
}

@SuppressLint("MissingPermission")
internal suspend fun get_location(activity: Activity) { 

    fusedClient = LocationServices.getFusedLocationProviderClient(activity)
    val location: Location? = suspendCancellableCoroutine { continuation ->
        fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, object : CancellationToken() {
            override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token
            override fun isCancellationRequested() = false})
            .addOnSuccessListener { loc: Location? ->
                continuation.resume(loc)
            }.addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
        }

        if (location != null) {
            latitude = location.latitude
            longitude = location.longitude
            api_location = "${latitude},${longitude}"
            // Call the suspendable useLocationUpdates, it will handle success/failure callbacks
            useLocationUpates(activity)
        } else {
            // Handle null location if necessary, perhaps call feed_failure
            (activity as? ApiResponse)?.feed_failure(Exception("Failed to get location"))
        }
}

internal suspend fun useLocationUpates(activity: Activity) {
    try {
        use_lat_and_long(activity)
        getJsonFromAPI(api_location)
        (activity as? ApiResponse)?.feed_success() // Cast to ApiResponse (MainActivity)
    } catch (e: Exception) {
        (activity as? ApiResponse)?.feed_failure(e)
    }
}

private suspend fun use_lat_and_long(context: Context) {
    withContext(Dispatchers.IO) {
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
}




