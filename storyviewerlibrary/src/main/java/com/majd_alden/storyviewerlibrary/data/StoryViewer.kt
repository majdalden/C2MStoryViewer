package com.majd_alden.storyviewerlibrary.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoryViewer(

    val phone: String = "",
    val  pictureUrl: String = "",
    val  viewDate : String = "",
    val fullName: String = ""
) : Parcelable