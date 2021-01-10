package com.app.androidwithreact2

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.weatherlibrary.Daily
import com.app.weatherlibrary.Main
import kotlinx.android.synthetic.main.weather_itrm.view.*
import kotlin.math.roundToInt

class WeatherListAdapter(var weatherList: List<Daily>) : RecyclerView.Adapter<WeatherListAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val textsListRow = LayoutInflater.from(parent.context)
            .inflate(R.layout.weather_itrm, parent, false) as ViewGroup

        return MyViewHolder(textsListRow)
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var weather = weatherList[position].weather[0].main
        setWeatherImage(weather, holder)

        val tempreture = String.format("%.1f", weatherList[position].temp.day - 273.15).toDouble()
        var weatherText = "$tempreture C"
        holder.layout.weatherItemTempreture.text = weatherText
    }

    private fun setWeatherImage(
        weather: Main,
        holder: MyViewHolder
    ) {
        when (weather) {
            Main.Clear -> holder.layout.weatherItemImage.setImageResource(R.drawable.clear)
            Main.Clouds -> holder.layout.weatherItemImage.setImageResource(R.drawable.cloudy)
            Main.Rain -> holder.layout.weatherItemImage.setImageResource(R.drawable.rain)
            else -> { // Note the block
                Log.e("error", "unrecognized weather type")
            }
        }
    }

    class MyViewHolder(val layout: ViewGroup) : RecyclerView.ViewHolder(layout)

}