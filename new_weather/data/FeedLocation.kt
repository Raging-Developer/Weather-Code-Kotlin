package com.app.new_weather.data

import org.json.JSONObject

//Never used, I get my location from the fused location provider.
class FeedLocation {
    companion object{
        var name: String? = null
        var region: String? = null
        var country: String? = null
        var lat: String? = null
        var lon: String? = null
        var localtime: String? = null
    }

    @Throws(Exception::class)
    fun populate(data: JSONObject) {
        name = data.optString("name")
        region = data.optString("region")
        country = data.optString("country")
        lat = data.optString("lat")
        lon = data.optString("lon")
        localtime = data.optString("localtime")
    }
}
