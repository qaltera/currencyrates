package com.qaltera.currencyrates.kmm.shared.cache

import com.qaltera.currencyrates.kmm.shared.cache.AppDatabase
import com.qaltera.currencyrates.kmm.shared.cache.DatabaseDriverFactory
import com.qaltera.currencyrates.kmm.shared.entity.CurrencyName
import com.qaltera.currencyrates.kmm.shared.entity.CurrencyRate
import com.qaltera.currencyrates.kmm.shared.entity.Source

internal class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = AppDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.appDatabaseQueries

    internal fun clearDatabase() {
        dbQuery.transaction {
            dbQuery.removeAllRates()
        }
    }

    internal fun getAllRates(): List<CurrencyRate> {
        return dbQuery.selectAllRates(::mapRateSelecting).executeAsList()
    }

    private fun mapRateSelecting(
        name: String,
        source: String,
        value: Float
    ): CurrencyRate {
        return CurrencyRate(
            name = CurrencyName.valueOf(name),
            source = Source.valueOf(source),
            rate = value
        )
    }

    fun insertRate(rate: CurrencyRate) {
        dbQuery.insertRate(
            name = rate.name.name,
            source = rate.source.name,
            value = rate.rate
        )
    }
}