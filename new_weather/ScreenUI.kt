package com.app.new_weather

import android.app.Activity
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.new_weather.ui.theme.New_WeatherTheme
import kotlinx.coroutines.launch

@Composable
//fun setComposableContent(composeView: ComposeView) {
//    composeView.apply {
//        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
//        BackGroundImage()
//    }
//}
fun setComposableContent(uiState: WeatherUiState) {
        BackGroundImage(uiState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackGroundImage(uiState: WeatherUiState) {
    val backImage = painterResource(R.drawable.night_time)
    val context = LocalContext.current
    val displayMetrics = context.resources.displayMetrics
    val height = displayMetrics.heightPixels
    val scrollState = rememberScrollState()
    val state = rememberPullToRefreshState()

    var isRefreshing by remember{ mutableStateOf(false) }
    val coScope = rememberCoroutineScope()
    val onRefresh: () -> Unit = {
        isRefreshing = true
        coScope.launch {
            try {
                get_location(context as Activity)
            } finally {
                isRefreshing = false
            }
        }
    }

    Image(painter = backImage,
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier.fillMaxSize()
    )

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = state)
    {
        //Scrollable content has to go inside the pullToRefreshBox
        //But I also need it to scroll differently in landscape. Hmm, think on it.
        Box(Modifier.verticalScroll(scrollState).height(height.dp),
        contentAlignment = Alignment.TopCenter)
        {
            WeatherLayout(uiState)
        }
    }
}

@Composable
fun WeatherLayout(uiState: WeatherUiState, modifier: Modifier = Modifier) {
    var lcondIcon = uiState.condIcon
    val iconImage = painterResource(R.drawable::class.java.getField("icon_$lcondIcon").getInt(null))

    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
            .padding(top = 60.dp)) //Make room for the toolbar
    {
        Image(painter = iconImage,
            contentDescription = null,
            modifier = Modifier.size(150.dp))
//            contentScale = ContentScale.Fit)

        //The only thing I want scrolling is fore_cast, except in landscape where it all scrolls
        Temp_today(uiState.temp_c,
            uiState.feelsLike,
            uiState.wind_dir,
            uiState.wind_mph)
        Condition_text(uiState.condText, uiState.pressure)
        Location_text(uiState.vm_place)
        Fore_cast(uiState.fore_array)
        SunRiseSet(uiState.sunrise, uiState.sunset)
        MoonPhase(uiState.moonphase, uiState.moonrise, uiState.moonset)

        Column(modifier = Modifier
                .fillMaxWidth()
//                .weight(1f) //I want it at the bottom of the screen not bottom of the layout.
                .align(Alignment.End)
        ) {}
        ApiLinkButton(uiState.post_code)
    }
}

@Composable
fun Temp_today( temp_c: String,
                feelslike_c: String,
                wind_dir: String,
                wind_mph: String,
                modifier: Modifier = Modifier) {
    Row(modifier = Modifier)
    {
        Text(
            text = "$temp_c\u00B0",
            fontSize = 20.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
            modifier = modifier.padding(0.dp),
            color = Color.LightGray
        )
        Chill_factor(feelslike_c, wind_dir, wind_mph)
    }
}

@Composable
fun Chill_factor(feelslike_c: String,
                 wind_dir: String,
                 wind_mph: String,
                 modifier: Modifier = Modifier) {
    Text(
        text = "(Which feels like $feelslike_c\u00B0 \nin a $wind_dir $wind_mph mph wind)",
        fontSize = 12.sp,
        lineHeight = 12.sp,
        textAlign = TextAlign.Center,
        modifier = modifier.padding(start = 12.dp),
        color = Color.LightGray)
}

@Composable
fun Condition_text(condText: String,
                   pressure_mb: String,
                   modifier: Modifier = Modifier) {
    Text(
        textAlign = TextAlign.Center,
        text = "The weather today is $condText at $pressure_mb mb",
        fontSize = 20.sp,
        lineHeight = 20.sp,
        modifier = modifier.padding(0.dp),
        color = Color.LightGray)
}

@Composable
fun Location_text(place: String,
                  modifier: Modifier = Modifier) {
    Text(
        text = "Which is not bad for $place",
        fontSize = 20.sp,
        lineHeight = 20.sp,
        textAlign = TextAlign.Center,
        modifier = modifier.padding(0.dp),
        color = Color.LightGray)
}

//Forecast has the next(?) three days in it.
@Composable
fun Fore_cast(fore_array: List<Fore_array_items>) {
    val tomoz_day = fore_array[1].date
    val tomoz_text = fore_array[1].conditionText

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Tomorrow(tomoz_text, tomoz_day)
        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
            items(fore_array.size){ index ->
                val item = fore_array[index]
                Text(text = "${item.date} ${item.conditionText}",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    overflow = TextOverflow.Visible)
            }
        }
    }
}

@Composable
fun Tomorrow(
    tomoz_text: String,
    tomoz_day: String,
    modifier: Modifier = Modifier)
{
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Tomorrow $tomoz_day will be\n $tomoz_text",
            fontSize = 15.sp,
            lineHeight = 15.sp,
            textAlign = TextAlign.Center,
            modifier = modifier.padding(10.dp),
            color = Color.LightGray)
    }
}

@Composable
fun SunRiseSet(sunrise: String,
               sunset: String,
               modifier: Modifier = Modifier) {
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
fun MoonPhase( moonphase: String,
               moonrise: String,
               moonset: String,
               modifier: Modifier = Modifier){
    val moonIcon = remember(moonphase){
        when (moonphase) {
            "First Quarter" -> "firstquarter"
            "Full Moon" -> "fullmoon"
            "Last Quarter" -> "lastquarter"
            "New Moon" -> "newmoon"
            "Waning Crescent" -> "waningcrescent"
            "Waning Gibbous" -> "waninggibbous"
            "Waxing Crescent" -> "waxingcrescent"
            "Waxing Gibbous" -> "waxinggibbous"
            else -> {
                "waxingcrescent"
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        var moon_text = "With the moon rising at $moonrise and setting at $moonset"

        if (moonrise == "No moonrise"){
            moon_text = "The moon is up and will set at $moonset"
        }

        Text(
            text = "The phase of the moon is $moonphase",
            fontSize = 12.sp,
            lineHeight = 12.sp,
            color = Color.LightGray,
            modifier = modifier.padding(top = 10.dp))
        Image(
            painter = painterResource(R.drawable::class.java.getField(moonIcon).getInt(null)),
            contentDescription = null,
            modifier = Modifier.size(125.dp)
        )
        Text(
            text = moon_text,
            fontSize = 12.sp,
            lineHeight = 12.sp,
            color = Color.LightGray)
    }
}

@Composable
fun ApiLinkButton(post_code: String) {
    val context = LocalContext.current
    val icon = painterResource(R.drawable.weatherapi_logo)
    val i = Intent()

    i.setAction(Intent.ACTION_VIEW)
    i.addCategory(Intent.CATEGORY_BROWSABLE)
    i.setData(Uri.parse("https://www.weatherapi.com/weather/q/$post_code"))

    TextButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = { context.startActivity(i) },
        content = {
            Image(
                painter = icon,
                contentDescription = "Link",
                modifier = Modifier
                    .size(40.dp)
                    .weight(1f)
            )
        })
}



