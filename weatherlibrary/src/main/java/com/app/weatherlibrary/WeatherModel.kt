package com.app.weatherlibrary

data class TotalWeather (
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Long,
    val current: Current,
    val minutely: List<Minutely>,
    val hourly: List<Current>,
    val daily: List<Daily>
)

data class Current (
    val dt: Long,
    val sunrise: Long? = null,
    val sunset: Long? = null,
    val temp: Double,
    val feels_like: Double,
    val pressure: Long,
    val humidity: Long,
    val dew_point: Double,
    val uvi: Double,
    val clouds: Long,
    val visibility: Long,
    val wind_speed: Double,
    val wind_deg: Long,
    val weather: List<Weather>,
    val pop: Double? = null
)

data class Weather (
    val id: Long,
    val main: Main,
    val description: Description,
    val icon: String
)

enum class Description(val value: String) {
    BrokenClouds("broken clouds"),
    ClearSky("clear sky"),
    FewClouds("few clouds"),
    LightRain("light rain"),
    OvercastClouds("overcast clouds"),
    ScatteredClouds("scattered clouds");

    companion object {
        public fun fromValue(value: String): Description = when (value) {
            "broken clouds"    -> BrokenClouds
            "clear sky"        -> ClearSky
            "few clouds"       -> FewClouds
            "light rain"       -> LightRain
            "overcast clouds"  -> OvercastClouds
            "scattered clouds" -> ScatteredClouds
            else               -> throw IllegalArgumentException()
        }
    }
}

enum class Main(val value: String) {
    Clear("Clear"),
    Clouds("Clouds"),
    Rain("Rain");

    companion object {
        public fun fromValue(value: String): Main = when (value) {
            "Clear"  -> Clear
            "Clouds" -> Clouds
            "Rain"   -> Rain
            else     -> throw IllegalArgumentException()
        }
    }
}

data class Daily (
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Temp,
    val feels_like: FeelsLike,
    val pressure: Long,
    val humidity: Long,
    val dew_point: Double,
    val wind_speed: Double,
    val wind_deg: Long,
    val weather: List<Weather>,
    val clouds: Long,
    val pop: Double,
    val rain: Double? = null,
    val uvi: Double
)

data class FeelsLike (
    val day: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)

data class Temp (
    val day: Double,
    val min: Double,
    val max: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)

data class Minutely (
    val dt: Long,
    val precipitation: Long
)
