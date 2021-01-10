package com.app.androidwithreact2

import android.content.Context
import android.text.format.DateFormat
import com.app.weatherlibrary.TotalWeather
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.Math.abs
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.collections.ArrayList

class WeatherCache(var context: Context, var cacheFileName: String = "weatherCache2.json") {
    //local date time - doesnt work with gson

    var lastWeatherResults = ArrayList<Pair<Date, TotalWeather>>()
    val MAX_WEATHERS_TO_SAVE = 20

    init {
        loadLastWeatherResultsFromCache()
    }

    fun add(weatherResult: Pair<Date, TotalWeather>){
        lastWeatherResults.add(weatherResult)

        if(lastWeatherResults.size > MAX_WEATHERS_TO_SAVE){
            lastWeatherResults.removeAt(0)
        }
    }

    fun getLastSimilarWeatherResult(
        latitude: Double,
        longitude: Double
    ): Pair<Date, TotalWeather>? {
        val currentDay = DateFormat.format("dd", Date()) as String
        val currentMonth = DateFormat.format("MMM", Date()) as String

        for(timeWeatherPair in lastWeatherResults){
            val resultDay = DateFormat.format("dd", timeWeatherPair.first) as String
            val resultMonth = DateFormat.format("MMM", timeWeatherPair.first) as String
            if(abs(timeWeatherPair.second.lat - latitude) < 0.001 &&    //because losses percision when parsed to and from json
                abs(timeWeatherPair.second.lon - longitude) < 0.001 &&
                resultDay == currentDay &&
                resultMonth == currentMonth){
                return timeWeatherPair
            }
        }
        return null
    }

    fun saveLastWeatherResultsToCache(): CompletableFuture<Void>? {
        val gson = Gson()
        val lastWeatherResults = gson.toJson(lastWeatherResults)
        return CompletableFuture.runAsync {
            Common.writeFileToInternalStorageSyncronously(context, cacheFileName, lastWeatherResults)
        }
    }

    fun loadLastWeatherResultsFromCache(): CompletableFuture<Void>? {
        return CompletableFuture.runAsync {
            var cacheText = Common.readFileFromInternalStorage(context, cacheFileName)

            val gson = Gson()
            val type =
                object : TypeToken<ArrayList<Pair<Date, TotalWeather>>>() {}.type
            lastWeatherResults = gson.fromJson(cacheText, type)
            var x = 4
            x+= 1
        }
    }


}