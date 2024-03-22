package com.app.new_weather

import com.app.new_weather.data.Astronomy
import com.app.new_weather.data.Condition
import org.json.JSONArray

data class LatLong (var latitude: Double,
                    var longitude: Double,
                    var place: String,
                    var astro: Astronomy,
                    var cond: Condition,
                    var temp: String,
                    var chill: String,
                    var cond_text: String,
                    var icon: String,
                    var forecast_day: JSONArray,
                    var wind_mph: String,
                    var wind_dir: String) {
    companion object {
        //The location settings were not working on the emulator, so do this.
        //Ho Chi Minh city just because it is unusual
//        double latitude = 10.777416;
//        double longitude = 106.639366;
        //new york for the sceptic tanks
//        double latitude = 40.7127;
//        double longitude = -74.0059;
        //New zealand because I am cyclone chasing
//        double latitude = -38.140693;
//        double longitude = 176.253784;
        //Local
//        double latitude = 53.5333;
//        double longitude = -2.2833;
        var latitude: Double = 10.777416
        var longitude: Double = 106.639366
        var place: String? = ""
        var astro: Astronomy? = Astronomy()
        var cond: Condition? = Condition()
        var temp: String = ""
        var chill: String = ""
        var cond_text: String = ""
        var icon: String = ""
        var forecast_day = JSONArray()
        var wind_mph = ""
        var wind_dir = ""
    }
}
