package com.app.new_weather.data

import org.json.JSONObject

class Astronomy {
    companion object{
        var sunrise: String? = null
        var sunset: String? = null
        var moonrise: String? = null
        var moonset: String? = null
        var moonphase: String? = null
        var moon_illumination: String? = null
        var is_moon_up = 0
        var is_sun_up = 0
    }

    fun populate(data: JSONObject) {
        sunrise = data.optString("sunrise")
        sunset = data.optString("sunset")
        moonrise = data.optString("moonrise")
        moonset = data.optString("moonset")
        moonphase = data.optString("moon_phase")
        moon_illumination = data.optString("moon_illumination")
        is_moon_up = data.optInt("is_moon_up")
        is_sun_up = data.optInt("is_sun_up")
    }
}
