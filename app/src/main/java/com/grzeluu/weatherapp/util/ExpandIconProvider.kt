package com.grzeluu.weatherapp.util

import android.widget.ImageView
import com.grzeluu.weatherapp.R

class ExpandIconProvider {
    companion object {
        fun ImageView.setExpandIcon(isExpanded: Boolean) {
            when (isExpanded) {
                true -> this.setImageResource(R.drawable.ic_expand_less)
                false -> this.setImageResource(R.drawable.ic_expand_more)
            }
        }
    }
}