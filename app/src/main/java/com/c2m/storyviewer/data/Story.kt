package com.c2m.storyviewer.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.regex.Matcher
import java.util.regex.Pattern

@Parcelize
data class Story(val url: String, val storyDate: Long) : Parcelable {

    //    fun isVideo() = url.contains(".mp4") || isYoutubeUrl(url)
    fun isVideo() = url.contains(".mp4")

    private fun getVideoIdFromYoutubeUrl(youtubeUrl: String?): String? {
        /*
           Possibile Youtube urls.
           http://www.youtube.com/watch?v=WK0YhfKqdaI
           http://www.youtube.com/embed/WK0YhfKqdaI
           http://www.youtube.com/v/WK0YhfKqdaI
           http://www.youtube-nocookie.com/v/WK0YhfKqdaI?version=3&hl=en_US&rel=0
           http://www.youtube.com/watch?v=WK0YhfKqdaI
           http://www.youtube.com/watch?feature=player_embedded&v=WK0YhfKqdaI
           http://www.youtube.com/e/WK0YhfKqdaI
           http://youtu.be/WK0YhfKqdaI
        */

        if (youtubeUrl?.trim().isNullOrEmpty()) return null

        val youtubeUrl = youtubeUrl?.trim() ?: ""

        val pattern =
            "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*"
        val compiledPattern: Pattern = Pattern.compile(pattern)
        //url is youtube url for which you want to extract the id.
        val matcher: Matcher = compiledPattern.matcher(youtubeUrl)
        return if (matcher.find()) {
            matcher.group()
        } else null
    }

    private fun isYoutubeUrl(youtubeUrl: String?): Boolean {
        if (youtubeUrl?.trim().isNullOrEmpty()) return false

        val youtubeUrl = youtubeUrl?.trim() ?: ""

        val success: Boolean
        val pattern = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+"
        success = !youtubeUrl.trim().isNullOrEmpty() && youtubeUrl.matches(pattern.toRegex())
        return success
    }
}