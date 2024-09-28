package com.example.currencyexchange

import android.app.Activity
import android.os.Bundle
import android.util.TypedValue
import android.view.inputmethod.InputBinding
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
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

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Currency Exchange"

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
                runOnUiThread{
                    binding.baseCurrency.text = "Connection Failed"
                }
            }
        }
    }

    private fun updateUI(request: Request) {
        runOnUiThread {
                binding.lastUpdated.text = request.time_last_update_utc


                binding.currencyContainer.removeAllViews()

                val typedValue = TypedValue()
                theme.resolveAttribute(R.attr.colorOnBackground, typedValue, true)
                val colorOnBackground = typedValue.data

                for ((currency, rate) in request.rates) {
                    if (currency == request.base_code) {
                        continue
                    }

                    val textView = TextView(this).apply {
                        text = String.format("%s: %.2f", currency, rate)
                        textSize = 20f
                        setTextColor(colorOnBackground)
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            gravity = android.view.Gravity.CENTER_HORIZONTAL
                        }
                        gravity = android.view.Gravity.CENTER
                    }
                    binding.currencyContainer.addView(textView)
                }
        }
    }
}