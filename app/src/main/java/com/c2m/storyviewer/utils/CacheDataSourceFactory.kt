package com.c2m.storyviewer.utils

import android.content.Context
import com.c2m.storyviewer.R
import com.c2m.storyviewer.app.StoryApp
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSink
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import java.io.File


class CacheDataSourceFactory(context: Context, maxCacheSize: Long, maxFileSize: Long) :
    DataSource.Factory {
    private val context: Context

    private val defaultDatasourceFactory: DefaultDataSource.Factory
    private val maxFileSize: Long
    private val maxCacheSize: Long

    //    private lateinit var simpleCache: SimpleCache
    override fun createDataSource(): DataSource {
        val leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(maxCacheSize)
//        simpleCache = SimpleCache(File(context.cacheDir, "media"), evictor)

//        val leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(90 * 1024 * 1024)
//        val databaseProvider: DatabaseProvider = ExoDatabaseProvider(this)
        val databaseProvider: DatabaseProvider = StandaloneDatabaseProvider(context)
        if (StoryApp.simpleCache == null) {
            StoryApp.simpleCache = SimpleCache(
                File(context.cacheDir, "media"), leastRecentlyUsedCacheEvictor, databaseProvider
            )
//            StoryApp.simpleCache = SimpleCache(cacheDir, leastRecentlyUsedCacheEvictor, databaseProvider)
        }

        return CacheDataSource(
            StoryApp.simpleCache!!, defaultDatasourceFactory.createDataSource(),
            FileDataSource(), CacheDataSink(StoryApp.simpleCache!!, maxFileSize),
            CacheDataSource.FLAG_BLOCK_ON_CACHE or CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR, null
        )
    }

    fun delete(context: Context?) {
        try {
            StoryApp.simpleCache?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    init {
        this.context = context
        this.maxCacheSize = maxCacheSize
        this.maxFileSize = maxFileSize
        val userAgent = Util.getUserAgent(
            context,
            Util.getUserAgent(context, context.getString(R.string.app_tag))
        )
        val defaultHttpDataSource = DefaultHttpDataSource.Factory()
        defaultHttpDataSource.setUserAgent(userAgent)
        defaultDatasourceFactory = DefaultDataSource.Factory(this.context, defaultHttpDataSource)
    }
}