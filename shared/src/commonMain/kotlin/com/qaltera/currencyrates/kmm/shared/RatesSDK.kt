package com.qaltera.currencyrates.kmm.shared

import com.github.aakira.napier.Napier
import com.qaltera.currencyrates.kmm.shared.cache.Database
import com.qaltera.currencyrates.kmm.shared.cache.DatabaseDriverFactory
import com.qaltera.currencyrates.kmm.shared.entity.CurrencyName
import com.qaltera.currencyrates.kmm.shared.entity.CurrencyRate
import com.qaltera.currencyrates.kmm.shared.entity.CurrencyRateSet
import com.qaltera.currencyrates.kmm.shared.entity.MarketData
import com.qaltera.currencyrates.kmm.shared.entity.Source
import com.qaltera.currencyrates.kmm.shared.network.RatesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class RatesSDK (databaseDriverFactory: DatabaseDriverFactory) {

    private val database = Database(databaseDriverFactory)
    private val api = RatesApi()

    @Throws(Exception::class) suspend fun getRates(forceReload: Boolean): List<CurrencyRateSet> {
        val cachedRates = database.getAllRates()
        return if (cachedRates.isNotEmpty() && !forceReload) {
            Napier.d("RatesSDK", null, "rates are cached")
            val cbrfRates = cachedRates.filter { it.source == Source.CBRF }
            val cbrfUsd = cbrfRates.find { it.name == CurrencyName.USD }
            val cbrfEur = cbrfRates.find { it.name == CurrencyName.EUR }
            val moexRates = cachedRates.filter { it.source == Source.MOEX }
            val moexUsd = moexRates.find { it.name == CurrencyName.USD }
            val moexEur = moexRates.find { it.name == CurrencyName.EUR }
            ArrayList<CurrencyRateSet>().apply {
                if (cbrfUsd != null && cbrfEur != null) {
                    this.add(
                        CurrencyRateSet(usdRate = cbrfUsd, eurRate = cbrfEur,
                    source = Source.CBRF))
                }
                if (moexUsd != null && moexEur != null) {
                    this.add(CurrencyRateSet(usdRate = moexUsd, eurRate = moexEur,
                        source = Source.MOEX))
                }
            }
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

                val result = listOf(
                    CurrencyRateSet(
                        usdRate = cbrfRates[0],
                        eurRate = cbrfRates[1],
                        source = Source.CBRF
                    ),
                    CurrencyRateSet(
                        usdRate = moexRates[0],
                        eurRate = moexRates[1],
                        source = Source.MOEX
                    )
                )

                database.clearDatabase()
                result.forEach { rateSet ->
                    database.insertRate(rateSet.usdRate)
                    database.insertRate(rateSet.eurRate)
                }

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
