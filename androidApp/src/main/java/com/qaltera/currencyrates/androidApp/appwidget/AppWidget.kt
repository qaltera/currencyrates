package com.qaltera.currencyrates.androidApp.appwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import com.qaltera.currencyrates.androidApp.data.Rates
import com.qaltera.currencyrates.androidApp.utils.AppWidgetUtils
import com.qaltera.currencyrates.kmm.shared.RatesSDK
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * App widget provider class, to handle update broadcast intents and updates
 * for the app widget.
 */
abstract class AppWidget : AppWidgetProvider() {

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
            }.onSuccess { result ->
                val rates = AppWidgetUtils.flattenRates(result)
                val views = RemoteViews(
                    context.packageName,
                    getLayout()
                )

                AppWidgetUtils.setOpenAppIntent(views, context)
                updateValues(views, rates, context)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }.onFailure {
                Log.e("AppWidget", "error!! ", it)
            }
        }
    }

    abstract fun getLayout(): Int

    abstract fun updateValues(views: RemoteViews, rates: Rates,
        context: Context)

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