package com.example.parkingqr.utils

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object FormatCurrencyUtil {

    private const val MAX_LENGTH_ORDER_LINE_ITEM = 5

    fun formatNumberCeil(number: Double): String {
        val decimalFormat = DecimalFormat("#,##0.###", DecimalFormatSymbols(Locale.US))
        decimalFormat.roundingMode = RoundingMode.CEILING
        return decimalFormat.format(number)
    }
    fun formatNumberCeilAndTruncate(number: Double): String{
        return truncateText(formatNumberCeil(number))
    }

    private fun truncateText(text: String): String {
        return if (text.length <= MAX_LENGTH_ORDER_LINE_ITEM) {
            text
        } else {
            "${text.substring(0, MAX_LENGTH_ORDER_LINE_ITEM)}..."
        }
    }
}