package io.selja.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.ContextCompat
import io.selja.R
import java.util.*
import java.util.concurrent.TimeUnit

private const val RED = R.color.md_red_400
private const val GREEN = R.color.md_green_400
private const val ORANGE = R.color.md_amber_400

class TimeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {


    fun displayTime(endTimestamp: Long, detailedView: Boolean) {
        val timeDiffMs = endTimestamp - Calendar.getInstance().timeInMillis
        if (detailedView) {
            displayTimeWithDetails(timeDiffMs)
        } else {
            displayTimeForDiff(timeDiffMs)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayTimeWithDetails(timeDiffMs: Long) {
        val days = TimeUnit.MILLISECONDS.toDays(timeDiffMs).toInt()
        val hours = TimeUnit.MILLISECONDS.toHours(timeDiffMs).toInt()
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeDiffMs).toInt()

        if (days == 0) {
            text = if (hours == 0) {
                // Display minutes and seconds
                val seconds = TimeUnit.MILLISECONDS.toSeconds(timeDiffMs) - TimeUnit.MINUTES.toSeconds(minutes.toLong())
                val minText = resources.getString(R.string.minutes, minutes)
                val secText = resources.getString(R.string.seconds, seconds)

                "$minText $secText"
            } else {
                // Display hours and minutes
                val hourText = resources.getQuantityString(R.plurals.hours, hours, hours)
                val realMinutes = minutes - TimeUnit.HOURS.toMinutes(hours.toLong()).toInt()
                val minText = resources.getString(R.string.minutes, realMinutes)

                "$hourText $minText"
            }
            setTextColor(ContextCompat.getColor(context, RED))
            return
        }

        val dText = resources.getQuantityString(R.plurals.days, days, days)
        val realHours = hours - TimeUnit.DAYS.toHours(hours.toLong()).toInt()
        val hText = resources.getQuantityString(R.plurals.hours, realHours, realHours)

        text = "$dText $hText"
        setTextColor(ContextCompat.getColor(context, if (days >= 2) GREEN else ORANGE))
    }

    private fun displayTimeForDiff(timeDiffMs: Long) {
        val days = TimeUnit.MILLISECONDS.toDays(timeDiffMs).toInt()
        val hours = TimeUnit.MILLISECONDS.toHours(timeDiffMs).toInt()
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeDiffMs).toInt()

        val timeString = when {
            days >= 1 -> {
                resources.getQuantityString(R.plurals.days, days, days)
            }

            hours >= 1 -> {
                resources.getQuantityString(R.plurals.hours, hours, hours)
            }

            minutes >= 1 -> {
                resources.getString(R.string.minutes, minutes)
            }
            else -> {
                val seconds = TimeUnit.MILLISECONDS.toSeconds(timeDiffMs).toInt()
                resources.getString(R.string.seconds, seconds)
            }
        }

        val color = when {
            days >= 2 -> GREEN
            days >= 1 -> ORANGE
            else -> RED
        }

        setTextColor(ContextCompat.getColor(context, color))
        text = timeString
    }
}