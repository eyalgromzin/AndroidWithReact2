package com.app.androidwithreact2

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.weatherlibrary.TotalWeather
import com.app.weatherlibrary.WeatherApi
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {
    val PERMISSION__REQUEST_CODE = 100

    lateinit var weatherCache: WeatherCache

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION__REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && isAllPermissionsGranted(grantResults))) {
                    showWeather()
                }
            }
        }
    }

    private fun isAllPermissionsGranted(grantResults: IntArray): Boolean {
        for(grantResult in grantResults){
            if(grantResult != PackageManager.PERMISSION_GRANTED) return false
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weatherCache = WeatherCache(this)

        setContentView(R.layout.activity_main)

        var weatherPreviewContainer = weatherPreviewContainer
        mainActivityMainLayout.removeView(weatherPreviewContainer)

        initShowWeatherButton()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    @SuppressLint("MissingPermission")
    private fun initShowWeatherButton() {
        showWeatherButton.setOnClickListener {
            //check if exists in cache daily result with current position.
            showWeather()
        }
    }

    private fun showLoader(){
        loaderImage.visibility = View.VISIBLE
        Glide.with(this).load(R.drawable.loader).into(loaderImage)
    }

    private fun hideLoader(){
        loaderImage.visibility = View.INVISIBLE
    }



    private fun showWeather() {
        if (checkRequiredPermissions()) {
            requestRequiredPermissions()
        } else {
            val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null) {
                val longitude = location.longitude
                val latitude = location.latitude

                var similarRequest =
                    weatherCache.getLastSimilarWeatherResult(latitude, longitude)

                if (similarRequest != null) {
                    showWeatherInUI(similarRequest.second)
                } else {
                    getWeatherFromApiAndUpdateUI(latitude, longitude) {
                        weatherCache.add(Pair(Date(), it))     //local date time converted to {}
                        weatherCache.saveLastWeatherResultsToCache()
                    }
                }
            } else {
                AlertDialog.Builder(this).setTitle("Location")
                    .setMessage("couldnt get location, plz check if your GPS is working")
                    .show()
            }
    //                    }
        }
    }

    private fun requestRequiredPermissions() {
        val sdkVersion = Build.VERSION.SDK_INT
        if (sdkVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), PERMISSION__REQUEST_CODE
            )
        }
    }

    private fun checkRequiredPermissions(): Boolean {
        val sdkVersion = Build.VERSION.SDK_INT
        return sdkVersion <= Build.VERSION_CODES.LOLLIPOP_MR1 ||
                return (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED)
    }



    private fun getWeatherFromApiAndUpdateUI(
        latitude: Double,
        longitude: Double,
        onSuccess: (TotalWeather) -> Unit
    ) {
        showLoader()
        WeatherApi().getWeatherAsync(latitude, longitude, {
                showWeatherInUI(it)
                onSuccess(it)
                hideLoader()
            }, {
                hideLoader()
                AlertDialog.Builder(this).setTitle("Weather")
                    .setMessage("failed to get weather").show()
            }
        )
    }

    private fun showWeatherInUI(totalWeather: TotalWeather) {
        mainActivityMainLayout.removeView(showWeatherButton)

        mainActivityMainLayout.addView(weatherPreviewContainer)

        val weatherLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val viewAdapter = WeatherListAdapter(totalWeather.daily)
        weatherPreviewRecyclerView.adapter = viewAdapter
        weatherPreviewRecyclerView.layoutManager = weatherLayoutManager
    }
}
