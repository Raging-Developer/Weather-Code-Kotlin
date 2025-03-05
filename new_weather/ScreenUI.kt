package com.app.new_weather

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.app.new_weather.data.Current
import com.app.new_weather.data.ForcUIState
import com.app.new_weather.data.Forecast
import com.app.new_weather.ui.theme.New_WeatherTheme
import com.example.new_weather.R
import java.text.SimpleDateFormat

@Composable
fun setComposableContent(lQueryState: ForcUIState, composeView: ComposeView, context: MainActivity){
    composeView.apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        BackGroundImage(lQueryState, context)
    }
}

@Composable
fun BackGroundImage(lQueryState: ForcUIState, context: MainActivity) {
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
        WeatherLayout(lQueryState, context)
    }
}

@Composable
fun WeatherLayout(lQueryState: ForcUIState, context: MainActivity, modifier: Modifier = Modifier) {
    val lForcState = lQueryState.forcState
    val lCurrState = lQueryState.currState
    var condIcon = lCurrState.getCondition()?.getIcon().toString()

    var condString: String

    if (lCurrState.getCondition()?.getIcon() == null) {
        condIcon = "na"
    }
    else {
        condString = condIcon.substring(condIcon.length - 7)
        condIcon = condString.substring(0, 3)
    }

    //Getting dynamic value for resourceId is, according to Gemini, getIdentifier.
    // Which is not correct, this is the non-gemini version. Ugly but it works.
    val iconImage = painterResource(R.drawable::class.java.getField("icon_$condIcon").getInt(null))

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier)
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
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)) {}
        ApiLinkButton()
    }
}

@Composable
fun Temp_today(lCurrState: Current, modifier: Modifier = Modifier) {
    val temp = lCurrState.getTemp_c()

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
                color = Color.Cyan)
            Chill_factor(lCurrState)
        }
    }
}

@Composable
fun Chill_factor(lCurrState: Current, modifier: Modifier = Modifier) {
    val chillTemp = lCurrState.getFeelslike_c()
    val wind_mph = lCurrState.getWind_mph()
    val wind_dir = lCurrState.getWind_dir()

    val chill =
        "(Which feels like $chillTemp\u00B0 \nin as $wind_mph mph wind\ncoming from $wind_dir)"
    Text(
        text = chill,
        fontSize = 10.sp,
        lineHeight = 12.sp,
        textAlign = TextAlign.Center,
        modifier = modifier.padding(start = 10.dp),
        color = Color.Cyan)
}

@Composable
fun Condition_text(lCurrState: Current, modifier: Modifier = Modifier) {
    val cond_text = lCurrState.getCondition()?.getText()

    val condition = "The weather today is $cond_text"
    Text(
        textAlign = TextAlign.Center,
        text = condition,
        fontSize = 20.sp,
        lineHeight = 20.sp,
        modifier = modifier.padding(0.dp),
        color = Color.Cyan)
}

@Composable
fun Location_text(modifier: Modifier = Modifier) {
    val loc = "Which is not bad for $place"

    Text(
        text = loc,
        fontSize = 20.sp,
        lineHeight = 21.sp,
        textAlign = TextAlign.Center,
        modifier = modifier.padding(0.dp),
        color = Color.Cyan)
}

@Composable
fun Fore_cast(lForcState: Forecast) {
    var tomoz_day = ""
    var tomoz_text = ""
    var tomoz_date = ""
    var fore_array = mutableListOf<String>()
    val forecast_day = lForcState.getForecast_day()

    for (i in 0 until forecast_day.length()) {
        val o = forecast_day.getJSONObject(i)
        val oday = SimpleDateFormat("EEEE").format(o.getString("date_epoch").toLong() * 1000)
        val odate = o.getString("date")
        val otext = o.getJSONObject("day").getJSONObject("condition").getString("text")

        if (i == 1) {
            tomoz_text = otext
            tomoz_day = oday
            tomoz_date = odate
        }

        fore_array.add("$oday $odate $otext")
    }

    Box {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Tomorrow(tomoz_text, tomoz_day, tomoz_date)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                for (day in fore_array) {
                    Text(
                        text = day,
                        color = Color.Cyan,
                        fontSize = 12.sp,
                        overflow = TextOverflow.Visible
                    )
                }
            }
        }
    }
}

@Composable
fun Tomorrow(
    tomoz_text: String,
    tomoz_day: String,
    tomoz_date: String,
    modifier: Modifier = Modifier) {

    val tomorrow = "Tomorrow $tomoz_day $tomoz_date will be\n $tomoz_text"

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = tomorrow,
            fontSize = 20.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
            modifier = modifier.padding(10.dp),
            color = Color.Cyan)
    }
}

@Composable
fun SunRiseSet(lForcState: Forecast, modifier: Modifier = Modifier) {
    val sunrise = lForcState.getAstro()?.getSunrise()
    val sunset = lForcState.getAstro()?.getSunset()

    Column(horizontalAlignment = Alignment.CenterHorizontally)
    {
        Text(
            text = "Sunrise is at $sunrise",
            fontSize = 10.sp,
            color = Color.Cyan,
            modifier = modifier.padding(top = 10.dp))
        Text(
            text = "and sunset is at $sunset",
            fontSize = 10.sp,
            color = Color.Cyan)
    }
}

@Composable
fun ApiLinkButton() {
    val context = LocalContext.current
    val icon = painterResource(id = R.drawable.weatherapi_logo)
    val i = Intent()

    i.setAction(Intent.ACTION_VIEW)
    i.addCategory(Intent.CATEGORY_BROWSABLE)
    i.setData(Uri.parse("https://www.weatherapi.com/"))

    TextButton(
        onClick = { context.startActivity(i) },
        content = {
            Image(
                painter = icon,
                contentDescription = null)
        })
}
