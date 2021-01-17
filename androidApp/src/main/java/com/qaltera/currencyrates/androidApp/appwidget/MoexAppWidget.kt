package com.qaltera.currencyrates.androidApp.appwidget

import android.content.Context
import android.widget.RemoteViews
import com.qaltera.currencyrates.androidApp.R
import com.qaltera.currencyrates.androidApp.data.Rates
import com.qaltera.currencyrates.androidApp.utils.AppWidgetUtils

/*
 * ************************************************
 * MoexAppWidget
 * Date: 2021-01-17
 * ------------------------------------------------
 * Copyright (C) SPB TV AG 2007-2021 - All Rights Reserved
 * Author: Yulia Rogovaya
 * ************************************************
 */

class MoexAppWidget : AppWidget() {

    override fun updateValues(views: RemoteViews, rates: Rates,
        context: Context
    ) {
        AppWidgetUtils.updateMoexValues(
            views, rates.moexUsd, rates.moexEur, context
        )
        AppWidgetUtils.updateMoexBrentValue(
            views, rates.moexBrent, context
        )
    }

    override fun getLayout() = R.layout.app_widget_moex
}