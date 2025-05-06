package com.app.new_weather

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.new_weather.LatLong.Companion.place
import com.app.new_weather.LatLong.Companion.post_code
import com.app.new_weather.data.Current
import com.app.new_weather.data.ForcUIState
import com.app.new_weather.data.Forecast
import com.app.new_weather.ui.theme.New_WeatherTheme
import java.text.SimpleDateFormat

@Composable
fun setComposableContent(lQueryState: ForcUIState, composeView: ComposeView){
      composeView.apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
          BackGroundImage(lQueryState)
    }
}

@Composable
fun BackGroundImage(lQueryState: ForcUIState) {
    val backImage = painterResource(R.drawable.night_time)

    Box(
        contentAlignment = Alignment.TopCenter)
    {
        Image(
            painter = backImage,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        WeatherLayout(lQueryState)
    }
}

@Composable
fun WeatherLayout(lQueryState: ForcUIState, modifier: Modifier = Modifier) {
    val lForcState = lQueryState.forcState
    val lCurrState = lQueryState.currState
    var condIcon = lCurrState.condition?.getIcon()
    var condString: String

    if (lCurrState.condition?.getIcon() == null) {
        condIcon = "na"
    }
    else {
        condString = condIcon!!.substring(condIcon.length - 7)
        condIcon = condString.substring(0, 3)
    }
    //This is ugly, but it avoids getIdentifier
    val iconImage = painterResource(R.drawable::class.java.getField("icon_$condIcon").getInt(null))



    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(top = 60.dp) //Make room for the toolbar
                .verticalScroll(rememberScrollState()). height(625.dp))
    {
        Image(
            painter = iconImage,
            contentDescription = null,
            contentScale = ContentScale.Fit)

        //The only thing I want scrolling is fore_cast
        Temp_today(lCurrState)
        Condition_text(lCurrState)
        Location_text()
        Fore_cast(lForcState)
        SunRiseSet(lForcState)
        MoonPhase(lForcState)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)) {}
        ApiLinkButton()
    }
}

@Composable
fun Temp_today(lCurrState: Current, modifier: Modifier = Modifier) {
    val temp = lCurrState.temp_c

    Box {
        Row(
            modifier = Modifier)
        {
            Text(
                text = "$temp\u00B0",
                fontSize = 20.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                modifier = modifier.padding(0.dp),
                color = Color.LightGray)
            Chill_factor(lCurrState)
        }
    }
}

@Composable
fun Chill_factor(lCurrState: Current, modifier: Modifier = Modifier) {
    val chillTemp = lCurrState.feelslike_c
    val wind_mph = lCurrState.wind_mph
    val wind_dir = lCurrState.wind_dir

    val chill =
        "(Which feels like $chillTemp\u00B0 \nin a $wind_mph mph wind\ncoming from $wind_dir)"
    Text(
        text = chill,
        fontSize = 12.sp,
        lineHeight = 12.sp,
        textAlign = TextAlign.Center,
        modifier = modifier.padding(start = 12.dp),
        color = Color.LightGray)
}

@Composable
fun Condition_text(lCurrState: Current, modifier: Modifier = Modifier) {
    val cond_text = lCurrState.condition?.text

    val condition = "The weather today is $cond_text"
    Text(
        textAlign = TextAlign.Center,
        text = condition,
        fontSize = 20.sp,
        lineHeight = 20.sp,
        modifier = modifier.padding(0.dp),
        color = Color.LightGray)
}

@Composable
fun Location_text(modifier: Modifier = Modifier) {
    val loc = "Which is not bad for $place"

    Text(
        text = loc,
        fontSize = 20.sp,
        lineHeight = 20.sp,
        textAlign = TextAlign.Center,
        modifier = modifier.padding(0.dp),
        color = Color.LightGray)
}

@Composable
fun Fore_cast(lForcState: Forecast) {
    var tomoz_day = ""
    var tomoz_text = ""
    var fore_array = mutableListOf<String>()
    val forecast_day = lForcState.forecast_day

    for (i in 0 until forecast_day.length()) {
        val o = forecast_day.getJSONObject(i)
        val oday = SimpleDateFormat("EEEE, d MMM").format(o.getString("date_epoch").toLong() * 1000)
        val otext = o.getJSONObject("day").getJSONObject("condition").getString("text")

        if (i == 1) {
            tomoz_text = otext
            tomoz_day = oday
        }
        fore_array.add("$oday $otext")
    }
    
    Box {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Tomorrow(tomoz_text, tomoz_day)
            LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                for (day in fore_array) {
                    item {
                        Text(
                            text = day,
                            color = Color.LightGray,
                            fontSize = 12.sp,
                            overflow = TextOverflow.Visible
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Tomorrow(
    tomoz_text: String,
    tomoz_day: String,
    modifier: Modifier = Modifier) {

    val tomorrow = "Tomorrow $tomoz_day will be\n $tomoz_text"

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = tomorrow,
            fontSize = 15.sp,
            lineHeight = 15.sp,
            textAlign = TextAlign.Center,
            modifier = modifier.padding(10.dp),
            color = Color.LightGray)
    }
}

@Composable
fun SunRiseSet(lForcState: Forecast, modifier: Modifier = Modifier) {
    val sunrise = lForcState.astro?.sunrise
    val sunset = lForcState.astro?.sunset

    Column(horizontalAlignment = Alignment.CenterHorizontally)
    {
        Text(
            text = "Sunrise is at $sunrise and sunset is at $sunset",
            fontSize = 12.sp,
            lineHeight = 12.sp,
            color = Color.LightGray,
            modifier = modifier.padding(top = 10.dp))
    }
}

@Composable
fun MoonPhase(lForcState: Forecast, modifier: Modifier = Modifier){

    var moonIcon = when (lForcState.astro?.moonphase) {
        "First Quarter" -> "firstquarter"
        "Full Moon" -> "fullmoon"
        "Last Quarter" -> "lastquarter"
        "New Moon" -> "newmoon"
        "Waning Crescent" -> "waningcrescent"
        "Waning Gibbous" -> "waninggibbous"
        "Waxing Crescent" -> "waxingcrescent"
        "Waxing Gibbous" -> "waxinggibbous"
        else -> {
            "fullmoon"
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally)
    {
        Text(
            text = "The phase of the moon is ${lForcState.astro?.moonphase}",
            fontSize = 12.sp,
            lineHeight = 12.sp,
            color = Color.LightGray,
            modifier = modifier.padding(top = 10.dp))
        Image(
            painter = painterResource(R.drawable::class.java.getField("$moonIcon").getInt(null)),
            contentDescription = null,
            modifier = Modifier.size(125.dp)
        )
        Text(
            text = "With the moon rising at ${lForcState.astro?.moonrise} " +
                    "and setting at ${lForcState.astro?.moonset}",
            fontSize = 12.sp,
            lineHeight = 12.sp,
            color = Color.LightGray

        )
    }
}

@Composable
fun ApiLinkButton() {
    val context = LocalContext.current
    val icon = painterResource(R.drawable.weatherapi_logo)
    val i = Intent()

    i.setAction(Intent.ACTION_VIEW)
    i.addCategory(Intent.CATEGORY_BROWSABLE)
    i.setData(Uri.parse("https://www.weatherapi.com/weather/q/$post_code"))

    TextButton(
        onClick = { context.startActivity(i) },
        content = {
            Image(
                painter = icon,
                contentDescription = "Link",
                contentScale = ContentScale.Fit)
        })
}





