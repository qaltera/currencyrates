package com.qaltera.currencyrates.androidApp

import android.content.Context
import android.widget.TextView

/*
 * ************************************************
 * UiUtils
 * Date: 2021-01-16
 * ------------------------------------------------
 * Copyright (C) SPB TV AG 2007-2021 - All Rights Reserved
 * Author: Yulia Rogovaya
 * ************************************************
 */

object UiUtils {
    fun colorIdFromChange(change: Float?,
     neutralColor: Int = R.color.colorPrimaryText) = when {
        change == null || change == 0F -> neutralColor
        change > 0 -> R.color.green
        else -> R.color.red
    }

    fun colorIdFromChange(
        valueNew: Float?, valueOld: Float?
    ): Int {
        val change = valueNew?.let { valueNew ->
            valueOld?.let { valueOld ->
                valueNew - valueOld
            }
        }
        return colorIdFromChange(change)
    }

    fun formatValue(number: Float?, needSign: Boolean = false): String? {
        return number?.let {
            val pattern = if (needSign) {
                "%+.2f"
            } else {
                "%.2f"
            }
            String.format(pattern, number)
        }
    }
}