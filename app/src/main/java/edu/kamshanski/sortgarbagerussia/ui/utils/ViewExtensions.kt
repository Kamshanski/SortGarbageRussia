package edu.kamshanski.sortgarbagerussia.ui.utils

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@ExperimentalContracts
public inline fun View.visibleIf(condition: Boolean, customization: () -> Unit) {
    contract {
        callsInPlace(customization, InvocationKind.EXACTLY_ONCE)
    }

    if (condition) {
        visibility = VISIBLE
        customization()
    } else {
        visibility = GONE
    }
}