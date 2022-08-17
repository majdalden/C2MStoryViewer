package com.majd_alden.storyviewerlibrary.screen

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.SparseIntArray
import androidx.appcompat.app.AppCompatActivity
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.BuildConfig
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheWriter
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.majd_alden.storyviewerlibrary.callback.TouchCallbacks
import com.majd_alden.storyviewerlibrary.customview.PullDismissLayout
import com.majd_alden.storyviewerlibrary.customview.StoryPager2Adapter
import com.majd_alden.storyviewerlibrary.data.StoryUser
import com.majd_alden.storyviewerlibrary.databinding.ActivityStoryViewerBinding
import com.majd_alden.storyviewerlibrary.utils.CubeOutTransformer2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.File

class StoryViewerActivity : AppCompatActivity(),
    PageViewOperator {

    private lateinit var binding: ActivityStoryViewerBinding

    private lateinit var pagerAdapter2: StoryPager2Adapter
    private var currentPage: Int = 0
    private var isFirstTime = true

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            if (position == currentPage) {
                currentFragment()?.resumeCurrentStory()
                return
            }
            currentPage = position
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createCacheIfIsNull(applicationContext)

        progressState.clear()

        setUpPager()

        binding.rootPDL.setListener(object : PullDismissLayout.Listener {
            override fun onDismissed() {
                finish()
            }
        })

        binding.rootPDL.setmTouchCallbacks(object : TouchCallbacks {
            override fun touchUp() {
                currentFragment()?.resumeCurrentStory()
            }
        })
    }

    override fun onResume() {
        super.onResume()

        onShowListener?.invoke()

        binding.viewPager2.registerOnPageChangeCallback(onPageChangeCallback)
    }

    override fun onStop() {
        super.onStop()
        binding.viewPager2.unregisterOnPageChangeCallback(onPageChangeCallback)
    }

    override fun onDestroy() {
        super.onDestroy()

        onDismissListener?.invoke()
    }

    override fun backPageView() {
        if (binding.viewPager2.currentItem > 0) {
            try {
                fakeDrag(false)
            } catch (e: Exception) {
                //NO OP
            }
        }
    }

    override fun nextPageView() {
        if (binding.viewPager2.currentItem + 1 < (binding.viewPager2.adapter?.itemCount ?: 0)) {
            try {
                fakeDrag(true)
            } catch (e: Exception) {
                //NO OP
            }
        } else {
            //there is no next story
            val isFinish = onFinishListener?.invoke()
            if (onFinishListener == null || (onFinishListener != null && isFinish == true)) {
                finish()
            }
//            Toast.makeText(this, "All stories displayed.", Toast.LENGTH_LONG).show()
        }
    }

    private fun setUpPager() {
        val storyUserList =
            intent?.extras?.getParcelableArrayList<StoryUser>(ARG_STORIES_USERS_LIST)
                ?.toMutableList() ?: mutableListOf()

        if (storyUserList.isEmpty()) {
            val isFinish = onFinishListener?.invoke()
            if (onFinishListener == null || (onFinishListener != null && isFinish == true)) {
                finish()
            }
            return
        }

        var currentStoryPosition: Int? = null

        if (isFirstTime) {
            currentPage = intent?.extras?.getInt(ARG_CURRENT_USER_POSITION, 0) ?: 0
            currentStoryPosition = intent?.extras?.getInt(ARG_CURRENT_STORY_POSITION, 0)
        }

        preLoadStories(storyUserList)

        pagerAdapter2 = StoryPager2Adapter(
            fragmentManager = supportFragmentManager,
            lifecycle = lifecycle,
            storyList = storyUserList
        )

        if (isFirstTime && currentStoryPosition != null && currentStoryPosition > 0) {
            progressState.put(currentPage, currentStoryPosition)
        }

        isFirstTime = false

//        binding.viewPager2.isUserInputEnabled = false
        binding.viewPager2.adapter = pagerAdapter2
        binding.viewPager2.currentItem = currentPage
        binding.viewPager2.setPageTransformer(CubeOutTransformer2())
        binding.viewPager2.registerOnPageChangeCallback(onPageChangeCallback)
    }

    private fun preLoadStories(storyUserList: MutableList<StoryUser>) {
        val imageList = mutableListOf<String>()
        val videoList = mutableListOf<String>()

        storyUserList.forEach { storyUser ->
            storyUser.stories.forEach { story ->
                if (story.isVideo()) {
                    videoList.add(story.storyUrl)
                } else if (story.isImage()) {
                    imageList.add(story.storyUrl)
                }/* else if (story.isAudio()) {
                    // ignore
                }*/
            }
        }
        preLoadVideos(videoList)
        preLoadImages(imageList)
    }

    private fun preLoadVideos(videoList: MutableList<String>) {
        videoList.map { data ->
            lifecycleScope.async(Dispatchers.IO) {
                val dataUri = Uri.parse(data)
                val dataSpec = DataSpec
                    .Builder()
                    .setUri(dataUri)
                    .setPosition(0)
                    .setLength(500 * 1024)
                    .build()
                val listener =
                    CacheWriter.ProgressListener { requestLength, bytesCached, _ ->
                        val downloadPercentage = (bytesCached * 100.0
                                / requestLength)
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "preLoadVideos downloadPercentage: $downloadPercentage")
                        }
                    }
                val mCacheDataSource = CacheDataSource.Factory()
                    .setCache(simpleCache!!)
                    .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
                    .createDataSource()

                runCatching {
                    CacheWriter(
                        mCacheDataSource,
                        dataSpec,
                        null,
                        listener
                    ).cache()
                }.onFailure {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "preLoadVideos Error Message: ${it.message}")
                        Log.e(TAG, "preLoadVideos Error Exception: ", it)
                    }
                }
            }
        }
    }

    private fun preLoadImages(imageList: MutableList<String>) {
        imageList.forEach { imageStory ->
            Glide.with(this).load(imageStory).preload()
        }
    }

    private fun currentFragment(): StoryViewerFragment? {
        return pagerAdapter2.findFragmentByPosition(currentPage) as? StoryViewerFragment?
    }

    /**
     * Change ViewPage sliding programmatically(not using reflection).
     * https://tech.dely.jp/entry/2018/12/13/110000
     * What for?
     * setCurrentItem(int, boolean) changes too fast. And it cannot set animation duration.
     */
    private var prevDragPosition = 0

    private fun fakeDrag(forward: Boolean) {
        /*val itemCount = binding.viewPager2.adapter?.itemCount ?: return
        val currentItem = binding.viewPager2.currentItem
        if (forward){
            if (currentItem + 1 < itemCount) {
                binding.viewPager2.setCurrentItem(currentItem + 1, true)
            }else{
                Toast.makeText(this, "All stories displayed.", Toast.LENGTH_LONG).show()
            }
        }else{
            if (currentItem > 0) {
                binding.viewPager2.setCurrentItem(currentItem - 1, true)
            }else{
                Toast.makeText(this, "This is first story.", Toast.LENGTH_LONG).show()
            }
        }*/
        if (prevDragPosition == 0 && binding.viewPager2.beginFakeDrag()) {
            ValueAnimator.ofInt(0, binding.viewPager2.width).apply {
                duration = 400L
                interpolator = FastOutSlowInInterpolator()
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {}

                    override fun onAnimationEnd(animation: Animator?) {
                        removeAllUpdateListeners()
                        if (binding.viewPager2.isFakeDragging) {
                            binding.viewPager2.endFakeDrag()
                        }
                        prevDragPosition = 0
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                        removeAllUpdateListeners()
                        if (binding.viewPager2.isFakeDragging) {
                            binding.viewPager2.endFakeDrag()
                        }
                        prevDragPosition = 0
                    }

                    override fun onAnimationStart(p0: Animator?) {}
                })
                addUpdateListener {
                    if (!binding.viewPager2.isFakeDragging) return@addUpdateListener
                    val dragPosition: Int = it.animatedValue as Int
                    val dragOffset: Float =
                        ((dragPosition - prevDragPosition) * if (forward) -1 else 1).toFloat()
                    prevDragPosition = dragPosition
                    binding.viewPager2.fakeDragBy(dragOffset)
                }
            }.start()
        }
    }

    companion object {

        private const val TAG = "StoryViewerActivity"
        private const val ARG_STORIES_USERS_LIST = "STORIES_USERS_LIST"
        private const val ARG_CURRENT_USER_POSITION = "CURRENT_USER_POSITION"
        private const val ARG_CURRENT_STORY_POSITION = "CURRENT_STORY_POSITION"

        var simpleCache: SimpleCache? = null
        val progressState = SparseIntArray()

        fun createCacheIfIsNull(context: Context?) {
            if (simpleCache == null && context != null) {
                val leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(90 * 1024 * 1024)
                val databaseProvider: DatabaseProvider = StandaloneDatabaseProvider(context)

                simpleCache = SimpleCache(
                    File(
                        context.cacheDir,
                        "media"
                    ),
                    leastRecentlyUsedCacheEvictor,
                    databaseProvider
                )
            }
        }

        fun newInstance(
            context: Context,
            storiesUsersList: MutableList<StoryUser>,
        ): Intent {
            return newInstance(context, ArrayList(storiesUsersList))
        }

        fun newInstance(
            context: Context,
            storiesUsersList: MutableList<StoryUser>,
            currentUserPosition: Int? = null,
            currentStoryPosition: Int? = null,
        ): Intent {
            return newInstance(
                context,
                ArrayList(storiesUsersList),
                currentUserPosition,
                currentStoryPosition
            )
        }

        fun newInstance(
            context: Context,
            storiesUsersList: ArrayList<StoryUser>,
        ): Intent {
            return newInstance(context, ArrayList(storiesUsersList), null, null)
        }

        fun newInstance(
            context: Context,
            storiesUsersList: ArrayList<StoryUser>,
            currentUserPosition: Int? = null,
            currentStoryPosition: Int? = null,
        ): Intent {
            val storyViewerActivityIntent = Intent(context, StoryViewerActivity::class.java)
            val args = Bundle()
            args.putParcelableArrayList(ARG_STORIES_USERS_LIST, ArrayList(storiesUsersList))
            if (currentUserPosition != null) {
                args.putInt(ARG_CURRENT_USER_POSITION, currentUserPosition)
            }
            if (currentStoryPosition != null) {
                args.putInt(ARG_CURRENT_STORY_POSITION, currentStoryPosition)
            }
            storyViewerActivityIntent.putExtras(args)
//            storyViewerActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            storyViewerActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            storyViewerActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            return storyViewerActivityIntent
        }

        var onShowListener: (() -> Unit)? = null
        var onFinishListener: (() -> Boolean)? = null
        var onDismissListener: (() -> Unit)? = null
    }
}
