package com.qaltera.currencyrates.shared

import com.github.aakira.napier.DebugAntilog
import com.github.aakira.napier.Napier

actual class Platform actual constructor() {

    actual val platform: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}