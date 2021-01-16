package com.qaltera.currencyrates.androidApp

import android.content.Context
import android.widget.RemoteViews

/*
 * ************************************************
 * MoexCbrfAppWidget
 * Date: 2021-01-17
 * ------------------------------------------------
 * Copyright (C) SPB TV AG 2007-2021 - All Rights Reserved
 * Author: Yulia Rogovaya
 * ************************************************
 */

class MoexCbrfAppWidget : AppWidget() {

    override fun updateValues(views: RemoteViews, rates: Rates,
        context: Context
    ) {
        AppWidgetUtils.updateCbRfValues(
            views, rates.cbRfUsd, rates.cbrfEur, context
        )
        AppWidgetUtils.updateMoexValues(
            views, rates.moexUsd, rates.moexEur, context
        )
    }

    override fun getLayout() = R.layout.app_widget_cbrf_moex
}