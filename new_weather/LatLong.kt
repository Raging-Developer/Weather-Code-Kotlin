package com.app.new_weather

data class LatLong (var place: String)
{
    companion object {
        var place: String? = ""
        var post_code: String? = ""
        var latitude: Double = 0.0
        var longitude: Double = 0.0
        var api_location = "${latitude},${longitude}"
    }
}
