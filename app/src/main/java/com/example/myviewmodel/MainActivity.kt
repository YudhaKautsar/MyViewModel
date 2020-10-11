package com.example.myviewmodel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.lang.Exception
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: WeatherAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = WeatherAdapter()
        adapter.notifyDataSetChanged()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnCity.setOnClickListener {
            val city = editCity.text.toString()
            if (city.isEmpty()) return@setOnClickListener
            showLoading(true)
            setWeather(city)
        }

    }

    private fun showLoading(state: Boolean) {
        if (state) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }

    fun setWeather(cities: String) {
        val listItems = ArrayList<WeatherItems>()

        val apiKey = "838f88afc17ac353189cc1dd5025ced4"
        val url = "https://api.openweathermap.org/data/2.5/group?id=$cities&units=metric&appid=${apiKey}"

        val client = AsyncHttpClient()
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                try {
                    //parsing JSON
                    val result = String(responseBody)
                    val responseObject = JSONObject(result)
                    val list = responseObject.getJSONArray("list")

                    for (i in 0 until list.length()) {
                        val weather = list.getJSONObject(i)
                        val weatherItems = WeatherItems()
                        weatherItems.id = weather.getInt("id")
                        weatherItems.name = weather.getString("name")
                        weatherItems.currentWeather = weather.getJSONArray("weather").getJSONObject(0).getString("main")
                        weatherItems.description = weather.getJSONArray("weather").getJSONObject(0).getString("description")
                        val tempInKelvin = weather.getJSONObject("main").getDouble("temp")
                        val tempInCelcius = tempInKelvin - 273
                        weatherItems.temperature = DecimalFormat("##.##"). format(tempInCelcius)
                        listItems.add(weatherItems)
                    }

                    //set data ke adpater
                    adapter.setData(listItems)
                    showLoading(false)
                } catch (e: Exception) {
                    Log.d("Exception", e.message.toString())
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
                Log.d("onFailure", error.message.toString())
            }
        })
    }
}