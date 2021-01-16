package com.qaltera.currencyrates.androidApp

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.qaltera.currencyrates.kmm.shared.RatesSDK
import com.qaltera.currencyrates.kmm.shared.entity.CurrencyRate
import com.qaltera.currencyrates.kmm.shared.entity.Source
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * App widget provider class, to handle update broadcast intents and updates
 * for the app widget.
 */
class AppWidget : AppWidgetProvider() {

    /**
     * Update a single app widget.  This is a helper method for the standard
     * onUpdate() callback that handles one widget update at a time.
     *
     * @param context          The application context.
     * @param appWidgetManager The app widget manager.
     * @param appWidgetId      The current app widget id.
     */
    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        GlobalScope.launch {
            kotlin.runCatching {
                RatesSDK.getRates(forceReload = true)
            }.onSuccess { rates ->
                val moexRates = rates.find { it.source == Source.MOEX }
                val cbrfRates = rates.find { it.source == Source.CBRF }
                val cbRfUsd = cbrfRates?.usdRate as? CurrencyRate.CbrfCurrencyRate
                val cbRfEur = cbrfRates?.eurRate as? CurrencyRate.CbrfCurrencyRate
                val moexUsd = moexRates?.usdRate as? CurrencyRate.MoexCurrencyRate
                val moexEur = moexRates?.eurRate as? CurrencyRate.MoexCurrencyRate

                val views = RemoteViews(
                    context.packageName,
                    R.layout.app_widget_cbrf_moex
                )

                val intent = Intent(context, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    context, 0, intent, 0)
                views.setOnClickPendingIntent(R.id.root, pendingIntent)

                val usdCbrfStr = UiUtils.formatValue(
                    cbRfUsd?.rateTomorrow ?: cbRfUsd?.rateToday
                )
                views.setTextViewText(
                    R.id.value_usd_cbrf,
                    usdCbrfStr
                )
                val eurCbrfStr = UiUtils.formatValue(
                    cbRfEur?.rateTomorrow ?: cbRfEur?.rateToday
                )
                views.setTextViewText(
                    R.id.value_eur_cbrf,
                    eurCbrfStr
                )

                val colorUsdCbRf = ContextCompat.getColor(
                    context,
                    UiUtils.colorIdFromChange(
                        cbRfUsd?.rateTomorrow,
                        cbRfUsd?.rateToday
                    )
                )
                views.setTextColor(
                    R.id.value_usd_cbrf,
                    colorUsdCbRf
                )

                val colorEurCbRf = ContextCompat.getColor(
                    context,
                    UiUtils.colorIdFromChange(
                        cbRfEur?.rateTomorrow,
                        cbRfEur?.rateToday
                    )
                )
                views.setTextColor(
                    R.id.value_eur_cbrf,
                    colorEurCbRf
                )

                views.setTextViewText(
                    R.id.value_usd_moex,
                    UiUtils.formatValue(moexUsd?.rate)
                )
                views.setTextViewText(
                    R.id.value_eur_moex,
                    UiUtils.formatValue(moexEur?.rate)
                )

                val colorUsdMoex = ContextCompat.getColor(
                    context,
                    UiUtils.colorIdFromChange(moexUsd?.change)
                )
                views.setTextColor(
                    R.id.value_usd_moex,
                    colorUsdMoex
                )

                val colorEurMoex = ContextCompat.getColor(
                    context,
                    UiUtils.colorIdFromChange(moexEur?.change)
                )
                views.setTextColor(
                    R.id.value_eur_moex,
                    colorEurMoex
                )

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }.onFailure {
                Log.e("AppWidget", "error!! ", it)
            }
        }
    }

    /**
     * Override for onUpdate() method, to handle all widget update requests.
     *
     * @param context          The application context.
     * @param appWidgetManager The app widget manager.
     * @param appWidgetIds     An array of the app widget IDs.
     */
    override fun onUpdate(
        context: Context, appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them.
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

}