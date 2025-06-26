package org.beespoon.artemis.util.extensions

import java.text.NumberFormat
import java.util.*

fun Number.pluralForm(): String = if (this.toDouble() == 1.0) "" else "s"

fun Number.commaSeperated(includeDecimals: Boolean = false  ): String {
    val formatted: String = NumberFormat.getNumberInstance(Locale.UK).format(this)
    return if (includeDecimals) formatted else formatted.replace("\\.[0-9]+$".toRegex(), "")
}