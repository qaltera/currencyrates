package com.qaltera.currencyrates.kmm.shared.network

import com.qaltera.currencyrates.kmm.shared.entity.CbrfDataWrapper
import com.qaltera.currencyrates.kmm.shared.entity.MoexMarketDataWrapper
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.*
import io.ktor.http.ContentType
import kotlinx.serialization.json.Json

class RatesApi {
    private val httpClient = HttpClient {
        install(JsonFeature) {
            val json = Json { ignoreUnknownKeys = true
            isLenient = true
            }
            accept(ContentType.Application.Json)
            serializer = KotlinxSerializer(json)
        }
    }

    suspend fun getMoexCurrencyRates(): MoexMarketDataWrapper {
        return httpClient.get(RATES_MOEX_ENDPOINT)
    }

    suspend fun getCbrfCurrencyRates(): CbrfDataWrapper {
        return httpClient.get(RATES_CBRF_ENDPOINT)
    }

    suspend fun getBrentRates(): MoexMarketDataWrapper {
        return httpClient.get(RATES_BRENT_ENDPOINT)
    }

    companion object {
        private const val RATES_MOEX_ENDPOINT =
            "https://iss.moex.com/iss/engines/currency/markets/selt/securities.json?iss.only=marketdata&securities=CETS%3AUSD000UTSTOM%2CCETS%3AEUR_RUB__TOM"

        private const val RATES_CBRF_ENDPOINT =
            "https://iss.moex.com/iss/statistics/engines/currency/markets/selt/rates.json"

        private const val RATES_BRENT_ENDPOINT =
            "https://iss.moex.com/iss/engines/futures/markets/forts/securities/BRH1.json?iss.only=marketdata"
    }
}

