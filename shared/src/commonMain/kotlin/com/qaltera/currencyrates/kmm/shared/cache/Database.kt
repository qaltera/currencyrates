package com.qaltera.currencyrates.kmm.shared.cache

import com.qaltera.currencyrates.kmm.shared.cache.AppDatabase
import com.qaltera.currencyrates.kmm.shared.cache.DatabaseDriverFactory
import com.qaltera.currencyrates.kmm.shared.entity.CurrencyRate

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
        value: Float
    ): CurrencyRate {
        return CurrencyRate(
            name = name,
            rate = value
        )
    }

    fun insertRate(rate: CurrencyRate) {
        dbQuery.insertRate(
            name = rate.name,
            value = rate.rate
        )
    }
}