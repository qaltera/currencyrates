package com.qaltera.currencyrates.androidApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.qaltera.currencyrates.kmm.shared.RatesSDK
import com.qaltera.currencyrates.kmm.shared.cache.DatabaseDriverFactory
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import androidx.core.view.isVisible
import kotlinx.coroutines.cancel


class MainActivity : AppCompatActivity() {
    private val mainScope = MainScope()

    private lateinit var ratesList: RecyclerView
    private lateinit var progressBarView: FrameLayout
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val sdk = RatesSDK(DatabaseDriverFactory(this))

    private val ratesAdapter = RatesAdapter(listOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Currency rates"
        setContentView(R.layout.activity_main)

        ratesList = findViewById(R.id.ratesListRv)
        progressBarView = findViewById(R.id.progressBar)
        swipeRefreshLayout = findViewById(R.id.swipeContainer)

        ratesList.adapter = ratesAdapter
        ratesList.layoutManager = LinearLayoutManager(this)

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            displayRates(true)
        }

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
                sdk.getRates(needReload)
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