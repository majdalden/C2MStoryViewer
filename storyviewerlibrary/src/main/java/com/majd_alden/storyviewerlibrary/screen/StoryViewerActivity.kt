package com.majd_alden.storyviewerlibrary.screen

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.SparseIntArray
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheWriter
import com.majd_alden.storyviewerlibrary.app.StoryApp
import com.majd_alden.storyviewerlibrary.customview.StoryPagerAdapter
import com.majd_alden.storyviewerlibrary.data.StoryUser
import com.majd_alden.storyviewerlibrary.databinding.ActivityStoryViewerBinding
import com.majd_alden.storyviewerlibrary.utils.CubeOutTransformer
import kotlinx.coroutines.async

class StoryViewerActivity : AppCompatActivity(),
    PageViewOperator {

    /*private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }*/

    private lateinit var binding: ActivityStoryViewerBinding
    private lateinit var pagerAdapter: StoryPagerAdapter
    private var currentPage: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpPager()
    }

    override fun backPageView() {
        if (binding.viewPager.currentItem > 0) {
            try {
                fakeDrag(false)
            } catch (e: Exception) {
                //NO OP
            }
        }
    }

    override fun nextPageView() {
        if (binding.viewPager.currentItem + 1 < (binding.viewPager.adapter?.count ?: 0)) {
            try {
                fakeDrag(true)
            } catch (e: Exception) {
                //NO OP
            }
        } else {
            //there is no next story
//            finish()
            Toast.makeText(this, "All stories displayed.", Toast.LENGTH_LONG).show()
        }
    }

    private fun setUpPager() {
//        val storyUserList = StoryGenerator.generateStories()
        val storyUserList =
            intent?.extras?.getParcelableArrayList<StoryUser>(ARG_STORIES_USERS_LIST)
                ?.toMutableList() ?: mutableListOf()
        if (storyUserList.isEmpty()) {
            finish()
            return
        }
        preLoadStories(storyUserList)

        pagerAdapter = StoryPagerAdapter(
            supportFragmentManager,
            storyUserList
        )
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.currentItem = currentPage
        binding.viewPager.setPageTransformer(
            true,
            CubeOutTransformer()
        )
        binding.viewPager.addOnPageChangeListener(object : PageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
            }

            override fun onPageScrollCanceled() {
                currentFragment()?.resumeCurrentStory()
            }
        })
    }

    private fun preLoadStories(storyUserList: MutableList<StoryUser>) {
        val imageList = mutableListOf<String>()
        val videoList = mutableListOf<String>()

        storyUserList.forEach { storyUser ->
            storyUser.stories.forEach { story ->
                if (story.isVideo()) {
                    videoList.add(story.url)
                } else {
                    imageList.add(story.url)
                }
            }
        }
        preLoadVideos(videoList)
        preLoadImages(imageList)
    }

    private fun preLoadVideos(videoList: MutableList<String>) {
        videoList.map { data ->
            lifecycleScope.async {
                val dataUri = Uri.parse(data)
                /*val dataSpec = DataSpec(
                    dataUri,
                    0,
                    DataSpec.HTTP_METHOD_POST,
                    null,
                    emptyMap(),
                    0,
                    500 * 1024,
                    null,
                    0,
                    null,
                )*/

                /*val dataSpec = DataSpec(
                    dataUri,
                    0,
                    500 * 1024,
                    null
                )*/

                /*val dataSpec = DataSpec(
                    dataUri,
                    0,
                    500 * 1024,
                )*/

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
                        Log.d("preLoadVideos", "downloadPercentage: $downloadPercentage")
                    }

                try {
                    val mCacheDataSource = CacheDataSource.Factory()
                        .setCache(StoryApp.simpleCache!!)
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
                        it.printStackTrace()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
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
        return pagerAdapter.findFragmentByPosition(
            binding.viewPager,
            currentPage
        ) as StoryViewerFragment
    }

    /**
     * Change ViewPage sliding programmatically(not using reflection).
     * https://tech.dely.jp/entry/2018/12/13/110000
     * What for?
     * setCurrentItem(int, boolean) changes too fast. And it cannot set animation duration.
     */
    private var prevDragPosition = 0

    private fun fakeDrag(forward: Boolean) {
        if (prevDragPosition == 0 && binding.viewPager.beginFakeDrag()) {
            ValueAnimator.ofInt(0, binding.viewPager.width).apply {
                duration = 400L
                interpolator = FastOutSlowInInterpolator()
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {}

                    override fun onAnimationEnd(animation: Animator?) {
                        removeAllUpdateListeners()
                        if (binding.viewPager.isFakeDragging) {
                            binding.viewPager.endFakeDrag()
                        }
                        prevDragPosition = 0
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                        removeAllUpdateListeners()
                        if (binding.viewPager.isFakeDragging) {
                            binding.viewPager.endFakeDrag()
                        }
                        prevDragPosition = 0
                    }

                    override fun onAnimationStart(p0: Animator?) {}
                })
                addUpdateListener {
                    if (!binding.viewPager.isFakeDragging) return@addUpdateListener
                    val dragPosition: Int = it.animatedValue as Int
                    val dragOffset: Float =
                        ((dragPosition - prevDragPosition) * if (forward) -1 else 1).toFloat()
                    prevDragPosition = dragPosition
                    binding.viewPager.fakeDragBy(dragOffset)
                }
            }.start()
        }
    }

    companion object {
        val progressState = SparseIntArray()

        private const val TAG = "StoryViewerActivity"
        private const val ARG_STORIES_USERS_LIST = "STORIES_USERS_LIST"

        fun newInstance(
            context: Context,
            storiesUsersList: MutableList<StoryUser>,
        ): Intent {
            return newInstance(context, ArrayList(storiesUsersList))
        }

        fun newInstance(
            context: Context,
            storiesUsersList: ArrayList<StoryUser>,
        ): Intent {
            val storyViewerActivityIntent = Intent(context, StoryViewerActivity::class.java)
            val args = Bundle()
            args.putParcelableArrayList(ARG_STORIES_USERS_LIST, ArrayList(storiesUsersList))
            storyViewerActivityIntent.putExtras(args)
            return storyViewerActivityIntent
        }
    }
}
