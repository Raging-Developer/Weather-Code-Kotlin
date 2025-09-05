package com.app.new_weather

import androidx.lifecycle.ViewModel
import com.app.new_weather.data.Astronomy
import com.app.new_weather.data.Condition
import com.app.new_weather.data.Current
import com.app.new_weather.data.Forecast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Locale

// Data class for the forecast array
data class Fore_array_items(
    val date: String,
    val conditionText: String
)

// Data class to hold all weather information for the UI
data class WeatherUiState(
    val temp_c: String = "--",
    val condText: String = "Loading...",
    val condIcon: String? = "na",
    val vm_place: String = "Loading location...",
    val feelsLike: String = "--",
    val wind_dir: String = "",
    val wind_mph: String = "",
    val pressure: String = "",
    val fore_array: List<Fore_array_items> = emptyList(),
    val sunrise: String = "--:--",
    val sunset: String = "--:--",
    val moonphase: String = "Loading...",
    val moonrise: String = "--:--",
    val moonset: String = "--:--",
    val post_code: String = ""
)

class MainViewModel : ViewModel() {
    private val _showSuccessScreen = MutableStateFlow(false)
    val showSuccessScreen: StateFlow<Boolean> = _showSuccessScreen.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _weatherUiState = MutableStateFlow(WeatherUiState()) // Initial default state
    val weatherUiState: StateFlow<WeatherUiState> = _weatherUiState.asStateFlow()

    fun onSuccess() {
        _showSuccessScreen.value = true
        _errorMessage.value = null

        // Parse forecast_day JSONArray
        val fore_array_items = mutableListOf<Fore_array_items>()
        val forecastJsonArray = Forecast.forecast_day ?: JSONArray() // Use empty if null
        for (i in 0 until forecastJsonArray.length()) {
            try {
                val dayObject = forecastJsonArray.getJSONObject(i)
                val dateEpoch = dayObject.getString("date_epoch").toLong() * 1000
                val formattedDate = SimpleDateFormat("EEEE, d MMM", Locale.getDefault()).format(dateEpoch)
                val conditionText = dayObject.getJSONObject("day").getJSONObject("condition").getString("text")
                fore_array_items.add(Fore_array_items(formattedDate, conditionText))
            } catch (e: Exception) {
                // Handle or log parsing error for individual forecast item
            }
        }
        
        var iconName: String? = "na"
        Condition.condIcon?.let {
            if (it.length > 7) {
                iconName = it.substring(it.length - 7, it.length - 4) //You can do a substring in one, who knew?
            }
        }

        _weatherUiState.value = WeatherUiState(
            temp_c = "${Current.temp_c}",
            condText = Condition.condText ?: "N/A",
            condIcon = iconName,
            vm_place = LatLong.place ?: "N/A",
            feelsLike = "${Current.feelslike_c}",
            wind_dir = Current.wind_dir ?: "",
            wind_mph = "${Current.wind_mph} mph",
            pressure = "${Current.pressure_mb}",
            fore_array = fore_array_items,
            sunrise = Astronomy.sunrise ?: "N/A",
            sunset = Astronomy.sunset ?: "N/A",
            moonphase = Astronomy.moonphase ?: "N/A",
            moonrise = Astronomy.moonrise ?: "N/A",
            moonset = Astronomy.moonset ?: "N/A",
            post_code = LatLong.post_code ?: ""
        )
    }

    fun onFailure(exception: Exception?) {
        _showSuccessScreen.value = false // Ensure success screen isn't shown
        // on error state keep stale data
        _errorMessage.value = "Ooops... could be a gateway problem, give it a swipe or rotate it and see what happens.\n ${exception?.message ?: ""}"
    }
}
