package io.selja.utils

import android.net.Uri
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.facebook.drawee.view.SimpleDraweeView
import io.selja.R
import io.selja.ui.custom.TimeView

@BindingAdapter("imageUrl")
fun bindImage(view: SimpleDraweeView, imageUrl: String) {
    view.setImageURI(Uri.parse(imageUrl))
}

@BindingAdapter("maxValue")
fun bindSpinnerMaxValue(view: Spinner, max: Int) {
    val elements = List(max) { i ->
        view.resources.getQuantityString(R.plurals.days, i + 1, i + 1)
    }

    view.adapter =
        ArrayAdapter<String>(view.context, R.layout.spinner_item_layout, elements)
}

@BindingAdapter("distanceText")
fun bindDistance(textView: TextView, distance: Double) {
    if (distance == 0.0) {
        textView.text = "-"
        return
    }

    textView.text = textView.resources.getString(R.string.km_unit, distance)
}

@BindingAdapter(value = ["timestamp", "detailedView"], requireAll = false)
fun bindTimestamp(timeView: TimeView, timestamp: Long, isDetailed: Boolean = false) {
    timeView.displayTime(timestamp, isDetailed)
}

