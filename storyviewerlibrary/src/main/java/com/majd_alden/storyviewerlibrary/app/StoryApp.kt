package com.majd_alden.storyviewerlibrary.app

import android.app.Application
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File

class StoryApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(90 * 1024 * 1024)
//        val databaseProvider: DatabaseProvider = ExoDatabaseProvider(this)
        val databaseProvider: DatabaseProvider = StandaloneDatabaseProvider(this)

        if (simpleCache == null) {
//            simpleCache = SimpleCache(cacheDir, leastRecentlyUsedCacheEvictor, databaseProvider)
            simpleCache = SimpleCache(
                File(cacheDir, "media"), leastRecentlyUsedCacheEvictor, databaseProvider

            )
        }
    }

    companion object {
        var simpleCache: SimpleCache? = null
    }
}