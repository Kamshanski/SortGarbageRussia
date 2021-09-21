package edu.kamshanski.sortgarbagerussia.ui.utils

import android.content.res.Resources
import android.util.TypedValue
import androidx.fragment.app.Fragment
import kotlin.math.roundToInt

// Методы для работы с ресурсами Android

/**
 * Convert [size] of [typedValue] to displayMetrics
 *
 * @param size - size in [typedValue] dimensions
 * @param typedValue - px, dp, sp...
 * @return displayMetrics
 */
fun Resources.convertDimensions(size: Int, typedValue: Int): Int {
    return TypedValue.applyDimension(
            typedValue,
            size.toFloat(),
            this.displayMetrics)
            .roundToInt()
}

/**
 * Converts [size] to display metrics
 *
 * @param size in dp
 * @return displayMetrics
 */
fun Resources.fromDp(size: Int): Int {
    return convertDimensions(size, TypedValue.COMPLEX_UNIT_DIP)
}

/**
 * Converts [size] to display metrics
 *
 * @param size in px
 * @return displayMetrics
 */
fun Resources.fromPx(size: Int): Int {
    return convertDimensions(size, TypedValue.COMPLEX_UNIT_PX)
}

fun Fragment.fromDp(size: Int) : Int = resources.fromDp(size)