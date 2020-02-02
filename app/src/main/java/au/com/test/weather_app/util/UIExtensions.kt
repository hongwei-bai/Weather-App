package au.com.test.weather_app.util

import android.view.View

fun View.gone() {
    visibility = View.GONE
}

fun View.show(flag: Boolean = true) {
    visibility = if (flag) View.VISIBLE else View.GONE
}

fun View.hide(gone: Boolean = false) {
    visibility = if (gone) View.GONE else View.INVISIBLE
}