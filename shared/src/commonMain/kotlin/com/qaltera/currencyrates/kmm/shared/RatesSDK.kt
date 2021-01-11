package com.qaltera.currencyrates.kmm.shared

import com.github.aakira.napier.Napier
import com.qaltera.currencyrates.kmm.shared.cache.DatabaseDriverFactory
import com.qaltera.currencyrates.kmm.shared.entity.CurrencyName
import com.qaltera.currencyrates.kmm.shared.entity.CurrencyRate
import com.qaltera.currencyrates.kmm.shared.entity.CurrencyRateSet
import com.qaltera.currencyrates.kmm.shared.entity.MarketData
import com.qaltera.currencyrates.kmm.shared.entity.Source
import com.qaltera.currencyrates.kmm.shared.network.RatesApi
import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime
import com.soywiz.klock.DateTimeTz
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class RatesSDK(databaseDriverFactory: DatabaseDriverFactory) {

    //private val database = Database(databaseDriverFactory)
    private val api = RatesApi()

    @Throws(Exception::class) suspend fun getRates(forceReload: Boolean): List<CurrencyRateSet> {
//        val cachedRates = database.getAllRates()
//        return if (cachedRates.isNotEmpty() && !forceReload) {
//            Napier.d("RatesSDK", null, "rates are cached")
//            val cbrfRates = cachedRates.filter { it.source == Source.CBRF }
//            val cbrfUsd = cbrfRates.find { it.name == CurrencyName.USD }
//            val cbrfEur = cbrfRates.find { it.name == CurrencyName.EUR }
//            val moexRates = cachedRates.filter { it.source == Source.MOEX }
//            val moexUsd = moexRates.find { it.name == CurrencyName.USD }
//            val moexEur = moexRates.find { it.name == CurrencyName.EUR }
//            ArrayList<CurrencyRateSet>().apply {
//                if (cbrfUsd != null && cbrfEur != null) {
//                    this.add(
//                        CurrencyRateSet(
//                            usdRate = cbrfUsd, eurRate = cbrfEur,
//                            source = Source.CBRF
//                        )
//                    )
//                }
//                if (moexUsd != null && moexEur != null) {
//                    this.add(
//                        CurrencyRateSet(
//                            usdRate = moexUsd, eurRate = moexEur,
//                            source = Source.MOEX
//                        )
//                    )
//                }
//            }
//        } else {
            Napier.d("RatesSDK", null, "requesting rates")
            return runBlocking {
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
                        usdRate = cbrfRates[1],
                        eurRate = cbrfRates[0],
                        source = Source.CBRF
                    ),
                    CurrencyRateSet(
                        usdRate = moexRates[1],
                        eurRate = moexRates[0],
                        source = Source.MOEX
                    )
                )

//                database.clearDatabase()
//                result.forEach { rateSet ->
//                    database.insertRate(rateSet.usdRate)
//                    database.insertRate(rateSet.eurRate)
//                }

                result
//            }
        }
    }

    private fun fromCbrfMarketData(marketData: MarketData): List<CurrencyRate> {
        return marketData.let { marketData ->
            println("size=" + marketData.data[0].size)

            val usdRate = marketData.data[0][3]?.floatValue
            val usdChange = marketData.data[0][4]?.floatValue
            val usdTradeDate = marketData.data[0][5]?.stringValue

            val eurRate = marketData.data[0][6]?.floatValue
            val eurChange = marketData.data[0][7]?.floatValue
            val eurTradeDate = marketData.data[0][8]?.stringValue

            val (usdTod, usdTom) =
                getTodTomValues(usdTradeDate, usdRate, usdChange)
            val (eurTod, eurTom) =
                getTodTomValues(eurTradeDate, eurRate, eurChange)

            listOf(
                CurrencyRate.CbrfCurrencyRate(
                    rateToday = eurTod ?: 0F ,
                    rateTomorrow = eurTom,
                    CurrencyName.EUR
                ),
                CurrencyRate.CbrfCurrencyRate(
                    rateToday = usdTod ?: 0f,
                    rateTomorrow = usdTom,
                    CurrencyName.USD
                )
            )
        }
    }

    private fun getTodTomValues(
        tradeDate: String?,
        rate: Float?,
        change: Float?): Pair<Float?, Float?> {
        val usdTradeDate = tradeDate?.let {
            val date = DateFormat.parse(it)
            date.local.date
        }
        val dateNow = DateTimeTz.nowLocal().local.date
        var rateToday: Float? = null
        var rateTomorrow: Float? = null
        usdTradeDate?.let { usdTradeDate ->
            if (usdTradeDate == dateNow) {
                Napier.d("trade date is today rate=$rate")
                rateToday = rate
            } else if (usdTradeDate > dateNow) {
                Napier.d("usdTradeDate=$usdTradeDate dateNow=$dateNow")
                Napier.d("rate=$rate change=$change")
                rateTomorrow = rate
                rateToday = (rate ?: 0F) - (change ?: 0F)
                Napier.d("rateToday=$rateToday")
            }
        }

        return Pair(rateToday, rateTomorrow)
    }

    private fun fromMoexMarketData(marketData: MarketData): List<CurrencyRate> {
        return marketData.let { marketData ->
            println("size=" + marketData.data[0].size)
            listOf(
                CurrencyRate.MoexCurrencyRate(
                    rate = marketData.data[0][8]?.floatValue ?: 0f,
                    name = CurrencyName.EUR,
                    change = marketData.data[0][28]?.floatValue
                ),
                CurrencyRate.MoexCurrencyRate(
                    rate = marketData.data[1][8]?.floatValue ?: 0f,
                    name = CurrencyName.USD,
                    change = marketData.data[1][28]?.floatValue
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
