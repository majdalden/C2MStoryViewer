package com.majd_alden.storyviewerlibrary.utils

import android.content.Context
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
import com.majd_alden.storyviewerlibrary.R
import com.majd_alden.storyviewerlibrary.screen.StoryViewerActivity
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
        if (StoryViewerActivity.simpleCache == null) {
            StoryViewerActivity.simpleCache = SimpleCache(
                File(context.cacheDir, "media"), leastRecentlyUsedCacheEvictor, databaseProvider
            )
//            StoryViewerActivity.simpleCache = SimpleCache(cacheDir, leastRecentlyUsedCacheEvictor, databaseProvider)
        }

        return CacheDataSource(
            StoryViewerActivity.simpleCache!!, defaultDatasourceFactory.createDataSource(),
            FileDataSource(), CacheDataSink(StoryViewerActivity.simpleCache!!, maxFileSize),
            CacheDataSource.FLAG_BLOCK_ON_CACHE or CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR, null
        )
    }

    fun delete(context: Context?) {
        try {
            StoryViewerActivity.simpleCache?.release()
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