package com.majd_alden.storyviewerlibrary.utils

import android.os.Parcel
import android.view.View

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}


fun <T> Parcel.readList2(outVal: List<T>, loader: ClassLoader?): List<T> {
    this.readList(outVal, loader)
    return outVal
}