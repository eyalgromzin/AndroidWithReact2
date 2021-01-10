package com.app.weatherlibrary

import okhttp3.*
import android.os.AsyncTask
import com.google.gson.Gson
import java.lang.Exception


class WeatherApi {


    //e.g. https://api.openweathermap.org/data/2.5/onecall?lat=11&lon=11&cnt=$daysCount&appid=35101cae20c8e006a11fdbafac7a9097"
    companion object {
        //for usage with completableFuture / any other concurrency method.
        fun getDailyWeatherSynchronous(lat: Double, long: Double): WeatherResult {
            val apiKey = "35101cae20c8e006a11fdbafac7a9097"
            val getUrl =
                "https://api.openweathermap.org/data/2.5/onecall?lat=$lat&lon=$long&appid=$apiKey"

            val client = OkHttpClient()

            val request = Request.Builder().url(getUrl).build()

            val response = client.newCall(request).execute()

            return WeatherResult(response.code(), response.body()!!.string())
        }
    }

    //didnt want to create an interface just for success/fail - so i used function: (WeatherResult) -> Unit
    //to make the api always return to the thread it was called from, used asyncTask - so the user
    //wont have to deal later with Futures / suspend functions / other animals - saves time, writing for many people.

    data class WeatherResult(val exitCode: Int, var result: String)

    //caching - not the libraries job. because its the app that asks for permissions to the storage, to location. not the library.
    //if it was a server , then caching is good. because the server can store things. but its only a library.
    //i dont return a parsed object , because in case the call gets error 500, there is nothing to parse
    fun getWeatherAsync(lat:Double, long:Double,
                        onSuccess: (TotalWeather) -> Unit,
                        onFail: (String) -> Unit){
        GetWeatherAPIResponseAsync(lat, long, onSuccess, onFail).execute()
    }

    //in case of call failure the deveoper will get 500 withg the error message
    private class GetWeatherAPIResponseAsync(
        var lat: Double, var long: Double,
        var onSuccess: (TotalWeather) -> Unit,
        var onFail: (String) -> Unit) : AsyncTask<Void, Void, WeatherResult>() {

        override fun doInBackground(vararg params: Void?): WeatherResult {
            var res: WeatherResult
            try {
                res = getDailyWeatherSynchronous(lat, long)
            }catch (e: Exception){
                res = WeatherResult(500, e.toString())
            }

            return res
        }

        //returns back to the thread it was called from.
        override fun onPostExecute(result: WeatherResult) {
            super.onPostExecute(result)
            if(result.exitCode != 200){
                onFail(result.result)
            }else {
                var gson = Gson()
                val totalWeather = gson.fromJson(result.result, TotalWeather::class.java)
                onSuccess(totalWeather)
            }
        }
    }
}