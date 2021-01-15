package com.qaltera.currencyrates.kmm.shared.cache

import com.github.aakira.napier.Napier
import com.qaltera.currencyrates.kmm.shared.entity.CurrencyRateSet
import com.soywiz.klock.DateTime

/*
 * ************************************************
 * RatesCache
 * Date: 2021-01-15
 * ------------------------------------------------
 * Copyright (C) SPB TV AG 2007-2021 - All Rights Reserved
 * Author: Yulia Rogovaya
 * ************************************************
 */

class RatesCache {
    var rates: List<CurrencyRateSet>? = null
    var cacheTime: Long? = null

    fun getCachedRates() = rates?.let {
        if (isFresh()) {
            rates
        } else {
            null
        }
    }

    fun save(rates: List<CurrencyRateSet>) {
        cacheTime = DateTime.nowUnixLong()
        this.rates = rates
    }

    private fun isFresh() = cacheTime?.let { cacheTime ->
        DateTime.nowUnixLong() - cacheTime < CACHE_TIMEOUT_MS
    } ?: false

    companion object {
        const val CACHE_TIMEOUT_MS = 60*1000
    }
}