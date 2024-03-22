package com.app.new_weather.data

import org.json.JSONObject

class Astronomy {
    private var sunrise: String? = null
    private var sunset: String? = null
    private var moonrise: String? = null
    private var moonset: String? = null
    private var moonphase: String? = null
    private var moon_illumination: String? = null
    private var is_moon_up = 0
    private var is_sun_up = 0

    fun getSunrise(): String? {
        return sunrise
    }

    fun getSunset(): String? {
        return sunset
    }

    fun getMoonrise(): String? {
        return moonrise
    }

    fun getMoonset(): String? {
        return moonset
    }

    fun getMoonphase(): String? {
        return moonphase
    }

    fun getMoon_illumination(): String? {
        return moon_illumination
    }

    fun getIs_moon_up(): Int {
        return is_moon_up
    }

    fun getIs_sun_up(): Int {
        return is_sun_up
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
