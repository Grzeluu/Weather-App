package com.grzeluu.weatherapp.util

import android.view.View
import android.widget.TextView
import com.grzeluu.weatherapp.R

class TextViewUtils {
    companion object {
        fun TextView.setPoP(pop: Double) {
            with(this) {
                if (pop > 0) {
                    text = context.getString(
                        R.string.percent_of,
                        (pop * 100).toInt()
                    )
                    visibility = View.VISIBLE
                } else {
                    visibility = View.GONE
                }
            }
        }
    }
}