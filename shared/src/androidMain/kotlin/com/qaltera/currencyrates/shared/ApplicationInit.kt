package com.qaltera.currencyrates.shared

import com.github.aakira.napier.DebugAntilog
import com.github.aakira.napier.Napier

actual class ApplicationInit {
    actual fun init() {
        Napier.base(DebugAntilog())
    }
}