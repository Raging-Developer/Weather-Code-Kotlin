package com.app.new_weather

data class LatLong (var place: String)
{
    companion object {
        //The location settings were not working on the emulator, so do this.
        //Also, api_location cannot be empty on the first run through. No weather for the north pole 0,0

        var place: String? = ""
        var latitude: Double = 40.7127
        var longitude: Double = -74.0059
        var api_location = "${latitude},${longitude}"
    }
}
