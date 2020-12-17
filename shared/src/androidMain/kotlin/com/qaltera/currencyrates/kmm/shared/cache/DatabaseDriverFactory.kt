package com.qaltera.currencyrates.kmm.shared.cache

import android.content.Context
import com.github.aakira.napier.DebugAntilog
import com.github.aakira.napier.Napier
import com.qaltera.currencyrates.kmm.shared.cache.AppDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory(private val context: Context) {
    init {
        Napier.base(DebugAntilog())
    }

    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(AppDatabase.Schema, context, "test.db")
    }
}