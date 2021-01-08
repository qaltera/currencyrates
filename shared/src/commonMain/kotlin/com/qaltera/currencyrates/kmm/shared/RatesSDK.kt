package com.qaltera.currencyrates.kmm.shared

import com.github.aakira.napier.Napier
import com.qaltera.currencyrates.kmm.shared.cache.Database
import com.qaltera.currencyrates.kmm.shared.cache.DatabaseDriverFactory
import com.qaltera.currencyrates.kmm.shared.entity.CurrencyName
import com.qaltera.currencyrates.kmm.shared.entity.CurrencyRate
import com.qaltera.currencyrates.kmm.shared.entity.MarketData
import com.qaltera.currencyrates.kmm.shared.entity.RateSet
import com.qaltera.currencyrates.kmm.shared.entity.Source
import com.qaltera.currencyrates.kmm.shared.network.RatesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class RatesSDK (databaseDriverFactory: DatabaseDriverFactory) {

    private val database = Database(databaseDriverFactory)
    private val api = RatesApi()

    @Throws(Exception::class) suspend fun getRates(forceReload: Boolean): List<CurrencyRate> {
        val cachedRates = database.getAllRates()
        return if (cachedRates.isNotEmpty() && !forceReload) {
            Napier.d("RatesSDK", null, "rates are cached")
            cachedRates
        } else {
            Napier.d("RatesSDK", null, "requesting rates")
            runBlocking {
                val call1 = async { api.getMoexCurrencyRates() }
                val call2 = async { api.getCbrfCurrencyRates() }
                val moexResponse = call1.await()
                val cbrfResponse = call2.await()
                val moexRates =
                    fromMoexMarketData(moexResponse.marketdata)

                val cbrfRates =
                    fromCbrfMarketData(cbrfResponse.cbrf)

                val result = cbrfRates + moexRates

                database.clearDatabase()
                result.forEach { rate -> database.insertRate(rate)}

                result
            }
        }
    }

    private fun fromCbrfMarketData(marketData: MarketData): List<CurrencyRate> {
        return marketData.let { marketData ->
            println("size=" + marketData.data[0].size)
            listOf(
                CurrencyRate(
                    marketData.data[0][6]?.floatValue ?: 0f,
                    CurrencyName.EUR,
                    Source.CBRF
                ),
                CurrencyRate(
                    marketData.data[0][3]?.floatValue ?: 0f,
                    CurrencyName.USD,
                    Source.CBRF
                )
            )
        }
    }

    private fun fromMoexMarketData(marketData: MarketData): List<CurrencyRate> {
        return marketData.let { marketData ->
            println("size=" + marketData.data[0].size)
            listOf(
                CurrencyRate(
                    marketData.data[0][8]?.floatValue ?: 0f,
                    CurrencyName.EUR,
                    Source.MOEX
                ),
                CurrencyRate(
                    marketData.data[1][8]?.floatValue ?: 0f,
                    CurrencyName.USD,
                    Source.MOEX
                )
            )
        }
    }
//
//    @Throws(Exception::class) suspend fun getCurrencyRates():
//        List<CurrencyRate> {
//            return api.getCurrencyRates().marketdata.let { marketData ->
//                println("size=" + marketData.data[0].size)
//                listOf(
//                    CurrencyRate(
//                        marketData.data[0][8]?.floatValue ?: 0f,
//                        "eur"
//                    ),
//                    CurrencyRate(
//                        marketData.data[1][8]?.floatValue ?: 0f,
//                        "usd"
//                    )
//                )
//            }
//    }
}
