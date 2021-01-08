package com.qaltera.currencyrates.kmm.shared.entity

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder

@Serializable
data class MoexMarketDataWrapper(
    val marketdata: MarketData
)

@Serializable
data class CbrfDataWrapper(
    val cbrf: MarketData
)

@Serializable
data class MarketData(
    val columns: List<String>,
    val data: List<List<Value?>>
)

@Serializable
data class Value(
    val floatValue: Float?,
    val intValue: Int?,
    val stringValue: String?
) {
    @Serializer(forClass = Value::class)
    internal companion object : KSerializer<Value> {
        override fun deserialize(decoder: Decoder): Value {
            val str = decoder.decodeString()
            var intVal: Int? = null
            var floatVal: Float? = null
            try {
                floatVal = str.toFloat()
            } catch (ex: NumberFormatException) {
                println(ex)
            }
            if (floatVal == null) {
                try {
                    intVal = str.toInt()
                } catch (ex: NumberFormatException) {
                    println(ex)
                }
            }
            return Value(floatVal, intVal, str.takeIf
                { floatVal == null && intVal == null }
            )
        }
    }
}

data class RateSet(
    val moexRates: List<CurrencyRate>,
    val cbrfRates: List<CurrencyRate>
)

data class CurrencyRate(
    val rate: Float,
    val name: CurrencyName,
    val source: Source
)

enum class Source(source: String) {
    MOEX("moex"),
    CBRF("cbrf")
}

enum class CurrencyName(name: String) {
    USD("usd"),
    EUR("eur")
}




