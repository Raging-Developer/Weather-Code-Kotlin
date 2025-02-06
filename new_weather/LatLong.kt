package com.app.new_weather

data class LatLong (var place: String)
{
    companion object {
        //The location settings were not working on the emulator, so do this.
        //Also, api_location cannot be empty on the first run through. No weather for the north pole 0,0
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
        var place: String? = ""
        var latitude: Double = 40.7127
        var longitude: Double = -74.0059
        var api_location = "${latitude},${longitude}"
    }
}
