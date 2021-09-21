package edu.kamshanski.sortgarbagerussia.ui.utils

import android.text.ParcelableSpan
import android.text.Spannable
import android.text.SpannableStringBuilder

public inline fun SpannableStringBuilder.appendSpan(text: String, span: ParcelableSpan) {
    val start = length
    append(text)
    val end = length
    setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}