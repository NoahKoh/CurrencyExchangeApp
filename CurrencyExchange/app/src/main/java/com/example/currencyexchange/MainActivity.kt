package com.example.currencyexchange

import android.app.Activity
import android.os.Bundle
import android.view.inputmethod.InputBinding
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.currencyexchange.databinding.ActivityMainBinding
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fetchData().start()
    }

    private fun fetchData() : Thread
    {
        return Thread {
            val url = URL("https://open.er-api.com/v6/latest/sgd")
            val connection = url.openConnection() as HttpURLConnection

            if (connection.responseCode == 200)
            {
                val input = connection.inputStream
                val inputStreamReader = InputStreamReader(input, "UTF-8")
                val request = Gson().fromJson(inputStreamReader, Request::class.java)
                updateUI(request)
                inputStreamReader.close()
                input.close()
            }
            else
            {

                binding.baseCurrency.text = "Connection Failed"
            }
        }
    }

    private fun updateUI(request: Request) {
        runOnUiThread {
            kotlin.run {
                binding.lastUpdated.text = request.time_last_update_utc
                binding.usd.text = String.format("USD: %.2f", request.rates.USD)
                binding.eur.text = String.format("EUR: %.2f", request.rates.EUR)
                binding.jpy.text = String.format("JPY: %.2f", request.rates.JPY)
            }
        }
    }
}