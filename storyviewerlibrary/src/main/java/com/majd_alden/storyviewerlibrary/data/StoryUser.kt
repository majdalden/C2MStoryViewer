package com.majd_alden.storyviewerlibrary.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson

data class StoryUser(
    val username: String,
    val profilePicUrl: String,
    val stories: MutableList<Story>,
    val isShowMoreMenu: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        username = parcel.readString() ?: "",
        profilePicUrl = parcel.readString() ?: "",
        stories = Gson().fromJson(
            parcel.readString() ?: "", Array<Story>::class.java
        )?.toMutableList() ?: mutableListOf(),
        isShowMoreMenu = parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeString(profilePicUrl)
        parcel.writeString(Gson().toJson(stories.toTypedArray()))
        parcel.writeByte(if (isShowMoreMenu) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StoryUser> {
        override fun createFromParcel(parcel: Parcel): StoryUser {
            return StoryUser(parcel)
        }

        override fun newArray(size: Int): Array<StoryUser?> {
            return arrayOfNulls(size)
        }
    }
}