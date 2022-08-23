package com.majd_alden.storyviewerlibrary.data

import android.graphics.Typeface
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson

data class Story(
    val storyType: StoryType,
    val storyUrl: String = "",
    val storyText: String = "",
    val storyTextFont: StoryTextFont = StoryTextFont.DEFAULT,
    val storyTextBackgroundColor: String = "",
    val storyTextColor: String = "",
    val storyTextTypeface: Typeface? = null,
    val maxStoryTextLength: Int = 300,
    val maxStoryTextLines: Int = 10,
    val storyDate: Long,
    var id: Int? = null
) : Parcelable {

    /*constructor(parcel: Parcel) : this(
        storyType = parcel.readString()?.let { StoryType.valueOf(it) }
            ?: StoryType.IMAGE,
        storyUrl = if (storyType != StoryType.TEXT) parcel.readString() ?: "" else "",
        storyText = parcel.readString() ?: "",
        storyTextFont = parcel.readString()?.let { StoryTextFont.valueOf(it) }
            ?: StoryTextFont.DEFAULT,
        storyTextBackgroundColor = parcel.readString() ?: "",
        storyTextColor = parcel.readString() ?: "",
        storyTextTypeface = parcel.readString()?.let {
            try {
                Gson().fromJson(it, Typeface::class.java)
            } catch (e: Exception) {
                null
            }
        },
        maxStoryTextLength = parcel.readInt(),
        maxStoryTextLines = parcel.readInt(),
        storyDate = parcel.readLong()
    ) {
    }*/

    constructor(parcel: Parcel) : this(
        storyType = parcel.readString()?.let { StoryType.valueOf(it) }
            ?: StoryType.IMAGE,
        parcel = parcel
    )

    constructor(storyType: StoryType, parcel: Parcel) : this(
        storyType = storyType,
        storyUrl = if (storyType != StoryType.TEXT) parcel.readString() ?: "" else "",
        storyText = if (storyType == StoryType.TEXT) parcel.readString() ?: "" else "",
        storyTextFont = if (storyType == StoryType.TEXT) parcel.readString()
            ?.let { StoryTextFont.valueOf(it) }
            ?: StoryTextFont.DEFAULT else StoryTextFont.DEFAULT,
        storyTextBackgroundColor = if (storyType == StoryType.TEXT) parcel.readString()
            ?: "" else "",
        storyTextColor = if (storyType == StoryType.TEXT) parcel.readString() ?: "" else "",
        storyTextTypeface = if (storyType == StoryType.TEXT) parcel.readString()?.let {
            try {
                Gson().fromJson(it, Typeface::class.java)
            } catch (e: Exception) {
                null
            }
        } else null,
        maxStoryTextLength = parcel.readInt(),
        maxStoryTextLines = parcel.readInt(),
        storyDate = parcel.readLong(),
        id = parcel.readInt()

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(storyType.toString())
        if (storyType == StoryType.TEXT) {
            parcel.writeString(storyText)
            parcel.writeString(storyTextBackgroundColor)
            parcel.writeString(storyTextColor)
            if (storyTextTypeface != null) {
                parcel.writeString(Gson().toJson(storyTextTypeface))
            }
            parcel.writeInt(maxStoryTextLength)
            parcel.writeInt(maxStoryTextLines)
        } else {
            parcel.writeString(storyUrl)
        }
        parcel.writeLong(storyDate)
        id?.let { parcel.writeInt(it) }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Story> {
        override fun createFromParcel(parcel: Parcel): Story {
            return Story(parcel)
        }

        override fun newArray(size: Int): Array<Story?> {
            return arrayOfNulls(size)
        }
    }

    fun isVideo() = storyType == StoryType.VIDEO
    fun isImage() = storyType == StoryType.IMAGE
    fun isText() = storyType == StoryType.TEXT
    fun isAudio() = storyType == StoryType.AUDIO
}