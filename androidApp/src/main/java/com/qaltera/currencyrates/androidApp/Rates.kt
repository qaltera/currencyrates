package com.qaltera.currencyrates.androidApp

import com.qaltera.currencyrates.kmm.shared.entity.CurrencyRate

/*
 * ************************************************
 * Rates
 * Date: 2021-01-17
 * ------------------------------------------------
 * Copyright (C) SPB TV AG 2007-2021 - All Rights Reserved
 * Author: Yulia Rogovaya
 * ************************************************
 */

data class Rates (
    val cbRfUsd: CurrencyRate.CbrfCurrencyRate?,
    val cbrfEur: CurrencyRate.CbrfCurrencyRate?,
    val moexUsd: CurrencyRate.MoexCurrencyRate?,
    val moexEur: CurrencyRate.MoexCurrencyRate?,
    val moexBrent: CurrencyRate.MoexCurrencyRate?
)