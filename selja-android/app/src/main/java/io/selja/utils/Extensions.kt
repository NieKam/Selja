package io.selja.utils

import android.location.Location
import android.widget.EditText
import com.google.android.material.textfield.TextInputEditText


fun Location.toHumanReadableString() =
    "${String.format("%.2f", this.latitude)}, ${String.format("%.2f", this.longitude)}"

fun Location.toIntentString() = "geo:${this.latitude},${this.longitude}"

fun TextInputEditText.stringValue() = this.text.toString()

fun EditText.stringValue() = this.text.toString()

fun TextInputEditText.doubleValue() = try {
    this.text.toString().toDouble()
} catch (e: NumberFormatException) {
    0.0
}