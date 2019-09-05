package ir.nilva.abotorab.helper

import ir.nilva.abotorab.ApplicationContext

fun dp(dps: Int): Int {
    val scale = ApplicationContext.context.resources.displayMetrics.density
    return (dps * scale + 0.5f).toInt()
}