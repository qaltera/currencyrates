package com.qaltera.currencyrates.androidApp.activity

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qaltera.currencyrates.androidApp.R
import com.qaltera.currencyrates.androidApp.adapters.RatesAdapter
import com.qaltera.currencyrates.kmm.shared.RatesSDK
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private val mainScope = MainScope()

    private lateinit var toolbar: Toolbar
    private lateinit var ratesList: RecyclerView
    private lateinit var progressBarView: FrameLayout
    private lateinit var footerTextView: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val ratesAdapter = RatesAdapter(listOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.app_name)
        setContentView(R.layout.activity_main)
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimaryDark))
        );

        ratesList = findViewById(R.id.ratesListRv)
        toolbar = findViewById(R.id.toolbar)
        progressBarView = findViewById(R.id.progressBar)
        swipeRefreshLayout = findViewById(R.id.swipeContainer)
        footerTextView = findViewById(R.id.footer)

        toolbar.title = title

        ratesList.adapter = ratesAdapter
        ratesList.layoutManager = LinearLayoutManager(this)

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            displayRates(true)
        }
        val quotes = resources.getStringArray(R.array.quotes)
        val randomIndex: Int = Random.nextInt(quotes.size)
        footerTextView.text = quotes[randomIndex]

        displayRates(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
    }

    private fun displayRates(needReload: Boolean) {
        progressBarView.isVisible = true
        mainScope.launch {
            kotlin.runCatching {
                RatesSDK.getRates(needReload)
            }.onSuccess {
                ratesAdapter.rates = it
                ratesAdapter.notifyDataSetChanged()
            }.onFailure {
                Log.e("MainActivity", "error!! ", it)
                Toast.makeText(this@MainActivity, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
            progressBarView.isVisible = false
        }
    }
}