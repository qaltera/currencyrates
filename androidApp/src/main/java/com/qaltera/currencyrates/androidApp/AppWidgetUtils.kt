package com.qaltera.currencyrates.androidApp

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.qaltera.currencyrates.kmm.shared.entity.CurrencyRate
import com.qaltera.currencyrates.kmm.shared.entity.CurrencyRateSet
import com.qaltera.currencyrates.kmm.shared.entity.Source

/*
 * ************************************************
 * AppWidgetUtils
 * Date: 2021-01-17
 * ------------------------------------------------
 * Copyright (C) SPB TV AG 2007-2021 - All Rights Reserved
 * Author: Yulia Rogovaya
 * ************************************************
 */

object AppWidgetUtils {
    fun flattenRates(ratesSet: List<CurrencyRateSet>): Rates {
        val moexRates = ratesSet.find { it.source == Source.MOEX }
        val cbrfRates = ratesSet.find { it.source == Source.CBRF }
        val cbRfUsd = cbrfRates?.usdRate as? CurrencyRate.CbrfCurrencyRate
        val cbRfEur = cbrfRates?.eurRate as? CurrencyRate.CbrfCurrencyRate
        val moexUsd = moexRates?.usdRate as? CurrencyRate.MoexCurrencyRate
        val moexEur = moexRates?.eurRate as? CurrencyRate.MoexCurrencyRate
        val moexBrent = moexRates?.brentRate as? CurrencyRate.MoexCurrencyRate
        return Rates(
            cbRfUsd = cbRfUsd,
            cbrfEur = cbRfEur,
            moexUsd = moexUsd,
            moexEur = moexEur,
            moexBrent = moexBrent
        )
    }

    fun updateMoexBrentValue(
        views: RemoteViews,
        moexBrent: CurrencyRate.MoexCurrencyRate?,
        context: Context
    ) {
        views.setTextViewText(
            R.id.value_brent_moex,
            UiUtils.formatValue(moexBrent?.rate)
        )
        setColorToTextView(moexBrent?.change,
            R.id.value_brent_moex, views, context)
    }

    fun updateMoexValues(
        views: RemoteViews,
        moexUsd: CurrencyRate.MoexCurrencyRate?,
        moexEur: CurrencyRate.MoexCurrencyRate?,
        context: Context
    ) {
        views.setTextViewText(
            R.id.value_usd_moex,
            UiUtils.formatValue(moexUsd?.rate)
        )
        views.setTextViewText(
            R.id.value_eur_moex,
            UiUtils.formatValue(moexEur?.rate)
        )
        setColorToTextView(moexUsd?.change,
            R.id.value_usd_moex, views, context)
        setColorToTextView(moexEur?.change,
            R.id.value_eur_moex, views, context)
    }

    fun setOpenAppIntent(views: RemoteViews, context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, 0)
        views.setOnClickPendingIntent(R.id.root, pendingIntent)
    }

    fun updateCbRfValues(
        views: RemoteViews,
        cbRfUsd: CurrencyRate.CbrfCurrencyRate?,
        cbRfEur: CurrencyRate.CbrfCurrencyRate?,
        context: Context
    ) {
        setTextToTextView(cbRfUsd, R.id.value_usd_cbrf, views)
        setTextToTextView(cbRfEur, R.id.value_eur_cbrf, views)
        setColorToTextView(
            cbRfUsd, R.id.value_usd_cbrf, views, context
        )
        setColorToTextView(
            cbRfEur, R.id.value_eur_cbrf, views, context
        )
    }

    private fun setTextToTextView(
        rate: CurrencyRate.CbrfCurrencyRate?,
        viewId: Int,
        views: RemoteViews
    ) {
        val rateStr = UiUtils.formatValue(
            rate?.rateTomorrow ?: rate?.rateToday
        )
        views.setTextViewText(
            viewId,
            rateStr
        )
    }

    private fun setColorToTextView(change: Float?, viewId: Int,
        views: RemoteViews, context: Context) {
        val colorBrentMoex = ContextCompat.getColor(
            context,
            UiUtils.colorIdFromChange(change)
        )
        views.setTextColor(
            viewId,
            colorBrentMoex
        )
    }

    private fun setColorToTextView(
        currencyRate: CurrencyRate.CbrfCurrencyRate?,
        viewId: Int,
        views: RemoteViews,
        context: Context) {
        val colorUsdCbRf = ContextCompat.getColor(
            context,
            UiUtils.colorIdFromChange(
                currencyRate?.rateTomorrow,
                currencyRate?.rateToday
            )
        )
        views.setTextColor(
            viewId,
            colorUsdCbRf
        )
    }
}